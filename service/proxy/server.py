import asyncio
import logging
import socket
import traceback

from container import Container
from service.proxy.session import ProxySession


log = logging.getLogger(__name__)

class ProxyServer:
    def __init__(self, laddr, lport):
        self.ca_service = Container.ca_service()
        self.laddr = laddr
        self.lport = lport
        self.listen_sock = None
        self.sessions = {}

    def start(self):
        self.listen_sock = socket.create_server((self.laddr, self.lport))
        log.warning("proxy server is started on %s:%s", self.laddr, self.lport)
        self.listen_sock.setblocking(False)
        asyncio.run(self.accept())

    async def accept(self):
        loop = asyncio.get_event_loop()
        while True:
            try:
                (conn, addr) = await loop.sock_accept(self.listen_sock)
                conn.setblocking(False)
                session = ProxySession(conn)
                self.sessions[addr] = session
                session.start()
            except Exception as e:
                print('accept error:', traceback.format_exc())
                return



