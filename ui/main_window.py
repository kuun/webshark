import enum
import logging

from PyQt5.QtWidgets import QAction, QMainWindow, QMenu, QTabWidget, QDockWidget, QListWidget, QAbstractItemView, \
    QListWidgetItem, QStackedWidget, QWidget
from PyQt5.QtCore import Qt

from container import Container
from ui.ca_dialog import CADialog
from ui.history.history_view import HistoryView
from ui.proxy_settings import ProxySettingsWidget

log = logging.getLogger(__name__)

class Navs(enum.Enum):
    ProxySettings = 0
    SessionHistory = 1


class MainWindow(QMainWindow):
    def __init__(self):
        super(MainWindow, self).__init__()

        self.__init_ui()

    def __init_ui(self):
        self.stacked_widget = QStackedWidget(self)

        # add proxy settings widget to stack
        self.proxy_settings_widget = ProxySettingsWidget(self)
        self.proxy_settings_widget.layout().setContentsMargins(0, 0, 0, 0)
        self.stacked_widget.addWidget(self.proxy_settings_widget)

        # add history view to stack
        self.history_view = HistoryView(self)
        self.history_view.layout().setContentsMargins(0, 0, 0, 0)
        self.stacked_widget.addWidget(self.history_view)

        self.setCentralWidget(self.stacked_widget)

        self.__init_nav_list()

        self.setMinimumSize(1000, 700)
        self.setWindowTitle('Webshark')
        self.show()

    def __init_nav_list(self):
        dock = QDockWidget(self)
        dock.setTitleBarWidget(QWidget())
        dock.setAllowedAreas(Qt.LeftDockWidgetArea)
        dock.setFeatures(QDockWidget.NoDockWidgetFeatures)

        self.nav_list = QListWidget(dock)
        self.nav_list.setSelectionMode(QAbstractItemView.SingleSelection)
        self.nav_list.setSpacing(2)
        self.nav_list.currentItemChanged.connect(self.handle_nav_changed)

        item = QListWidgetItem(self.tr('Proxy Settings'), self.nav_list)
        item.setData(Qt.UserRole, Navs.ProxySettings)
        font = item.font()
        font.setPointSize(16)
        item.setFont(font)
        item.setSelected(True)

        item = QListWidgetItem(self.tr('Session History'), self.nav_list)
        item.setData(Qt.UserRole, Navs.SessionHistory)
        item.setFont(font)

        self.nav_list.activated.connect(self.stacked_widget.setCurrentIndex)

        dock.setWidget(self.nav_list)
        self.addDockWidget(Qt.LeftDockWidgetArea, dock)

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

    def handle_nav_changed(self, selected):
        nav: Navs = selected.data(Qt.UserRole)
        self.stacked_widget.setCurrentIndex(nav.value)

