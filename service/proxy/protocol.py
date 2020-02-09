import logging
import traceback
from asyncio import Transport, BaseTransport, Protocol
from typing import Optional

import httptools
from httptools import HttpParserUpgrade

from model.http_session import HttpSession

log = logging.getLogger(__name__)


class RequestProtocol(Protocol):
    def __init__(self, ca_service, history_model, loop):
        self.ca_service = ca_service
        self.history_model = history_model
        self.loop = loop
        self.parser = httptools.HttpRequestParser(self)
        self.server_transport: Transport = None
        self.client_transport: Transport = None
        self.server_transports = {}
        self.is_server_connected = False
        self.cached_data = []
        self.server_addr = None
        self.server_port = None
        self.need_write_version = True
        self.has_body = False
        self.is_chunked = False
        self.is_ssl = False
        self.session: HttpSession = None

    def get_session(self) -> HttpSession:
        return self.session

    def connection_made(self, transport: BaseTransport) -> None:
        self.client_transport = transport

    def connection_lost(self, exc: Optional[Exception]) -> None:
        # TODO: handle connection lost
        pass

    def pause_writing(self) -> None:
        pass

    def resume_writing(self) -> None:
        super().resume_writing()

    def data_received(self, data: bytes) -> None:
        if log.isEnabledFor(logging.DEBUG):
            log.debug('received data: %s', data)
        if self.session is None:
            self.session = HttpSession()
        self.session.add_request_data(data)
        try:
            self.parser.feed_data(data)
        except HttpParserUpgrade as e:
            log.debug('upgrade error: %s', e)
        except Exception as e:
            log.error('failed to parse http request data, error: %s', e)
            self.client_transport.close()
            if self.server_transport is not None:
                self.server_transport.close()

    def eof_received(self) -> Optional[bool]:
        if self.session is not None:
            self.record_session()
        return super().eof_received()

    def on_message_begin(self):
        self.need_write_version = True
        self.is_chunked = False

    def on_message_complete(self):
        if self.is_chunked:
            self.write_to_server(b'0\r\n\r\n')

    def on_url(self, data: bytes):
        method = self.parser.get_method()
        log.debug('on_url: %s', data)
        if method != b'CONNECT':
            if self.is_ssl:
                # not need to convert url for https request
                self.writelines_to_server([method, b' ', data, b' '])
                return
            url = httptools.parse_url(data)
            if url.host is None:
                log.debug('can not find target host, close client: %s',
                          self.client_transport.get_extra_info('socket').getpeername())
                self.client_transport.close()
                return
            self.server_addr = url.host.decode('utf-8')
            self.server_port = 80 if url.port is None else url.port
            log.debug('connect to server: %s:%s', self.server_addr, self.server_port)
            self.loop.create_task(self.connect_server(self.server_addr, self.server_port))
            # strip protocol and host in url
            # convert url format http://host/path => /path
            self.writelines_to_server([method,
                                       b' ',
                                       url.path,
                                       b' '])
            return
        self.is_ssl = True
        index = data.rfind(b':')
        if index == -1:
            log.error('invalid target address, data: %s', data)
            self.client_transport.close()
            return
        log.debug('data: %s', data)
        self.server_addr = data[:index].decode('utf-8')
        self.server_port = int(data[index + 1:])
        self.loop.create_task(self.connect_server(self.server_addr, self.server_port))

    def on_header(self, name: bytes, value: bytes):
        if self.need_write_version:
            self.write_http_version()
            self.need_write_version = False
        self.writelines_to_server([name, b': ', value, b'\r\n'])

    def on_headers_complete(self):
        if self.need_write_version:
            self.write_http_version()
        self.write_to_server(b'\r\n')

    def on_body(self, data: bytes):
        if self.is_chunked:
            data_len = '{:x}\r\n'.format(len(data))
            self.writelines_to_server([data_len.encode(), data, b'\r\n'])
        else:
            self.write_to_server(data)

    def on_chunk_header(self):
        self.is_chunked = True

    def write_to_server(self, data):
        if self.is_server_connected:
            self.server_transport.write(data)
        else:
            self.cached_data.append(data)

    def writelines_to_server(self, data_list):
        if self.is_server_connected:
            self.server_transport.writelines(data_list)
        else:
            self.cached_data.extend(data_list)

    def write_http_version(self):
        self.writelines_to_server([b'HTTP/', self.parser.get_http_version().encode(), b'\r\n'])

    async def connect_server(self, host, port):
        if self.server_transport is not None:
            return
        try:
            transport, _ = await self.loop.create_connection(lambda: ResponseProtocol(self),
                                                             host=host, port=port, ssl=self.is_ssl)
            log.debug('connected to server: %s:%s', host, port)
            self.server_transport = transport
            self.is_server_connected = True
            if self.is_ssl:
                response = b'HTTP/1.1 200 Connection established\r\n\r\n'
                self.session.add_response_data(response)
                self.client_transport.write(response)
                self.loop.create_task(self.upgrade_client_socket())
            else:
                log.debug(self.cached_data)
                self.server_transport.writelines(self.cached_data)
                self.cached_data = []
        except Exception as ex:
            response = b'HTTP/1.1 503 Service Unavailable\r\n\r\n'
            self.session.add_response_data(response)
            self.client_transport.write(response)
            self.client_transport.close()
            log.error('failed to connect to server %s:%s, error: %s', self.server_addr, self.server_port, ex)
        if self.is_ssl:
            self.record_session()

    async def upgrade_client_socket(self):
        try:
            log.debug('try to upgrade connection to tls, client: %s',
                      self.client_transport.get_extra_info('socket').getpeername())
            context = self.ca_service.get_ssl_context(self.server_addr)
            self.client_transport = await self.loop.start_tls(self.client_transport,
                                                              protocol=self, sslcontext=context,
                                                              server_side=True)
            self.server_transport.get_protocol().client_transport = self.client_transport
            log.debug('upgrade to tls success, server: %s:%s', self.server_addr, self.server_port)
        except Exception as ex:
            log.error('failed to upgrade transport to tls, server: %s:%s, error: %s',
                      self.server_addr, self.server_port, ex)
            self.client_transport.close()

    def record_session(self):
        self.history_model.add_session.emit(self.session)
        self.session = None


class ResponseProtocol(Protocol):
    def __init__(self, request_proto: RequestProtocol):
        self.request_proto = request_proto
        self.client_transport: Transport = request_proto.client_transport
        self.parser = httptools.HttpResponseParser(self)
        self.is_chunked = False
        self.session: HttpSession = None

    def connection_made(self, transport: BaseTransport) -> None:
        self.server_transport = transport

    def connection_lost(self, exc: Optional[Exception]) -> None:
        # TODO: handle connection lost
        pass

    def pause_writing(self) -> None:
        self.client_transport.pause_reading()

    def resume_writing(self) -> None:
        self.client_transport.resume_reading()

    def data_received(self, data: bytes) -> None:
        self.request_proto.get_session().add_response_data(data)
        try:
            self.parser.feed_data(data)
        except Exception as e:
            log.debug('data: %s', data)
            log.error('failed to parse http response data, error: %s', traceback.format_exc())
            self.server_transport.close()
            self.client_transport.close()

    def eof_received(self) -> Optional[bool]:
        self.server_transport.close()
        self.client_transport.close()
        return True

    # implements http parser protocol
    def on_message_begin(self):
        self.is_chunked = False

    def on_message_complete(self):
        if self.is_chunked:
            self.client_transport.write(b'0\r\n\r\n')
        self.request_proto.record_session()

    def on_status(self, data):
        self.client_transport.writelines([b'HTTP/',
                                          self.parser.get_http_version().encode(),
                                          b' ',
                                          str(self.parser.get_status_code()).encode(),
                                          b' ',
                                          data,
                                          b'\r\n'])

    def on_header(self, name: bytes, value: bytes):
        self.client_transport.writelines([name, b': ', value, b'\r\n'])

    def on_headers_complete(self):
        self.client_transport.write(b'\r\n')

    def on_body(self, data: bytes):
        if self.is_chunked:
            data_len = '{:x}\r\n'.format(len(data))
            self.client_transport.writelines([data_len.encode(), data, b'\r\n'])
        else:
            self.client_transport.write(data)

    def on_chunk_header(self):
        log.debug('chunk header')
        self.is_chunked = True
