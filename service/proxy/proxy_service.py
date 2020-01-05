import asyncio
from threading import Thread

from service.proxy.server import ProxyServer


class ProxyService(Thread):
    def __init__(self, laddr, lport):
        super().__init__()
        self.server = ProxyServer(laddr, lport)

    def run(self):
        self.server.start()


