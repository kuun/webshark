import asyncio
import logging
from asyncio.exceptions import CancelledError

from PyQt5.QtCore import QThread, pyqtSignal

from service.proxy.protocol import RequestProtocol

log = logging.getLogger(__name__)


class ProxyServer(QThread):
    failed = pyqtSignal(OSError)

    def __init__(self, ca_service, laddr, lport):
        super(ProxyServer, self).__init__()
        self.ca_service = ca_service
        self.laddr = laddr
        self.lport = lport
        self.listen_sock = None
        self.sessions = {}
        self.server = None
        self.loop = None

    def run(self):
        self.loop = asyncio.new_event_loop()
        # self.loop.set_debug(True)
        corotine = self.loop.create_server(lambda: RequestProtocol(self.ca_service, self.loop), host=self.laddr, port=self.lport)
        try:
            self.server = self.loop.run_until_complete(corotine)
            log.warning('proxy server is started on %s:%s', self.laddr, self.lport)
            self.loop.run_until_complete(self.server.serve_forever())
        except CancelledError as e:
            log.debug('proxy server is closed, error: %s', e)
        except Exception as e:
            log.error('event loop exit, error: %s', e)
            if self.server is not None:
                self.server.close()
            self.loop.close()
            self.failed.emit(e)
        log.warning('proxy server exit!')

    def close(self):
        self.loop.call_soon_threadsafe(self.server.close)
