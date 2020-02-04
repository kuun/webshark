from service.proxy.server import ProxyServer


class ProxyService:
    def __init__(self, ca_service):
        super().__init__()
        self.ca_service = ca_service

    def start(self, laddr, lport):
        self.server = ProxyServer(self.ca_service, laddr, lport)
        self.server.start()
