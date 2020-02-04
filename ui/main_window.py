import logging

from PyQt5.QtWidgets import QAction, QMainWindow, QMenu, QTabWidget

from container import Container
from ui.ca_dialog import CADialog

log = logging.getLogger(__name__)

class MainWindow(QMainWindow):
    def __init__(self):
        super(MainWindow, self).__init__()
        self.__init_ui()

    def __init_ui(self):
        self.create_menu()

        self.tab_widget = QTabWidget(self)
        self.tab_widget.setTabsClosable(True)
        self.setCentralWidget(self.tab_widget)

        self.setMinimumSize(1000, 700)
        self.setWindowTitle('Webshark')
        self.show()

    def create_menu(self):
        menu_bar = self.menuBar()
        file_menu: QMenu = menu_bar.addMenu(self.tr('&File'))

        connect_act = QAction(self.tr('&Certificate'), self)
        connect_act.triggered.connect(self.open_ca_dialog)
        file_menu.addAction(connect_act)

        start_act = QAction(self.tr('&Start proxy'), self)
        start_act.triggered.connect(self.start_proxy)
        file_menu.addAction(start_act)

    def open_ca_dialog(self):
        dialog = CADialog(self)
        dialog.show()

    def start_proxy(self):
        proxy_service = Container.proxy_service()
        proxy_service.start('127.0.0.1', 8080)

    def closeEvent(self, event):
        log.debug('close main window.')
        proxy_service = Container.proxy_service()
        proxy_service.close()
        event.accept()
