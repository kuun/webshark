from PyQt5.QtCore import QObject, pyqtSignal

from service.proxy.server import ProxyServer


class ProxyService(QObject):
    failed = pyqtSignal(OSError)

    def __init__(self, ca_service):
        super().__init__()
        self.ca_service = ca_service
        self.server = None

    def start(self, laddr, lport):
        self.server = ProxyServer(self.ca_service, laddr, lport)
        self.server.failed.connect(self.handle_failed)
        self.server.start()

    def close(self):
        if self.server is not None:
            self.server.close()
            self.server.wait()
            self.server = None

    def handle_failed(self, err):
        self.server = None
        self.failed.emit(err)
