import asyncio
import logging
import traceback
from enum import Enum
from socket import socket

log = logging.getLogger(__name__)

class SessionState(Enum):
    WAIT_REQ = 0
    WAIT_REQ_HEADER = 1
    WAIT_REQ_BODY = 2
    WAIT_RES_HEADER = 3
    WAIT_RES_BODY = 4


class ProxySession:
    def __init__(self, in_sock):
        self.in_sock: socket = in_sock

    def start(self):
        addr = self.in_sock.getpeername()
        asyncio.create_task(self.do_start(), name='session-{}:{}'.format(addr[0], addr[1]))

    async def do_start(self):
        loop = asyncio.get_event_loop()
        try:
            while True:
                data = await loop.sock_recv(self.in_sock, 2048)
                log.debug('data len: %s', len(data))
                if len(data) == 0:
                    return
        except Exception as e:
            log.error("session recv error: {}", traceback.format_exc())
            return

