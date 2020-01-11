import asyncio
import logging

from container import Container
from service.proxy.protocol import RequestProtocol

log = logging.getLogger(__name__)


class ProxyServer:
    def __init__(self, laddr, lport):
        self.ca_service = Container.ca_service()
        self.laddr = laddr
        self.lport = lport
        self.listen_sock = None
        self.sessions = {}

    def start(self):
        asyncio.run(self.do_start())

    async def do_start(self):
        loop = asyncio.get_event_loop()
        self.server = await loop.create_server(lambda: RequestProtocol(), host=self.laddr, port=self.lport)
        log.warning('proxy server is started on %s:%s', self.laddr, self.lport)
        await self.server.serve_forever()
        log.warning('proxy server exit!')


