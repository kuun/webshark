import logging

from httptools import HttpRequestParser, HttpResponseParser


log = logging.getLogger(__name__)

class HttpMessage:
    def __init__(self):
        self.start_time = 0.0
        self.end_time = 0.0
        self.version = ''
        self.content_type = ''
        self.headers = {}
        self.is_invalid = False
        self.raw_data = []

    def add_data(self, data):
        self.raw_data.append(data)

    def parse(self) -> bool:
        pass

    def on_message_begin(self):
        pass

    def on_header(self, name: bytes, value: bytes):
        _name = name.decode('utf-8')
        _value = value.decode('utf-8')
        self.headers[_name] = _value

    def on_headers_complete(self):
        pass


class HttpRequest(HttpMessage):
    def __init__(self):
        super().__init__()
        self.method = ''
        self.url = ''

    def on_url(self, url: bytes):
        self.url = url.decode('utf-8')

    def parse(self) -> bool:
        parser = HttpRequestParser(self)
        try:
            for data in self.raw_data:
                parser.feed_data(data)
            self.method = parser.get_method().decode('utf-8')
            self.version = parser.get_http_version()
            self.is_invalid = True
            return True
        except Exception as e:
            log.error('failed to parse http request message, error: %s', e)
            self.is_invalid = False
            return False



class HttpResponse(HttpMessage):
    def __init__(self):
        super().__init__()
        self.status_code = 0

    def parse(self) -> bool:
        parser = HttpResponseParser(self)
        try:
            for data in self.raw_data:
                parser.feed_data(data)
            self.status_code = parser.get_status_code()
            self.version = parser.get_http_version()
            self.is_invalid = True
            return True
        except Exception as e:
            log.error('failed to parse http response message, error: %s', e)
            self.is_invalid = False
            return False

class HttpSession:
    def __init__(self):
        self.request = HttpRequest()
        self.response= HttpResponse()

    def add_request_data(self, data):
        self.request.add_data(data)

    def add_response_data(self, data):
        self.response.add_data(data)

    def get_request(self) -> HttpRequest:
        return self.request

    def get_response(self) -> HttpResponse:
        return self.response

    def get_method(self) -> str:
        if self.request is not None:
            return self.request.method
        return ''

    def get_url(self) -> str:
        if self.request is not None:
            return self.request.url
        return ''

    def get_request_content_type(self) -> str:
        if self.request is not None:
            return self.request.content_type
        return ''

    def get_response_content_type(self) -> str:
        if self.response is not None:
            return self.response.content_type
        return ''

    # if there is no invalid response code, return 0
    def get_status_code(self) -> int:
        if self.response is not None:
            return self.response.status_code
        return 0

    def get_start_time(self) -> float:
        if self.request is not None:
            return self.request.start_time
        return 0.0

    def get_end_time(self) -> float:
        if self.response is not None:
            return self.response.end_time
        # maybe the session has no response.
        if self.request is not None:
            return self.request.end_time
        return 0.0

