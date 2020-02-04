import asyncio
import logging
from threading import Thread
import traceback

from service.proxy.protocol import RequestProtocol

log = logging.getLogger(__name__)


class ProxyServer(Thread):
    def __init__(self, ca_service, laddr, lport):
        super().__init__()
        self.ca_service = ca_service
        self.laddr = laddr
        self.lport = lport
        self.listen_sock = None
        self.sessions = {}

    def run(self):
        self.loop = asyncio.new_event_loop()
        corotine = self.loop.create_server(lambda: RequestProtocol(self.ca_service, self.loop), host=self.laddr, port=self.lport)
        self.server = self.loop.run_until_complete(corotine)
        try:
            log.warning('proxy server is started on %s:%s', self.laddr, self.lport)
            self.loop.run_forever()
        except Exception:
            log.error('event loop exit, error: %s', traceback.format_exc())
        log.warning('proxy server exit!')

