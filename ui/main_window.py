from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import (QMainWindow, QMenu, QAction,
                             QDockWidget, QTabWidget, QListWidget)

from ui.ca_manage_tab import CAManageTab


class MainWindow(QMainWindow):
    def __init__(self):
        super(MainWindow, self).__init__()
        self.__init_ui()

    def __init_ui(self):
        self.create_menu()

        self.tab_widget = QTabWidget(self)
        self.tab_widget.setTabsClosable(True)
        self.setCentralWidget(self.tab_widget)
        self.tab_widget.addTab(CAManageTab(self.tab_widget), self.tr('CA Management'))

        self.setMinimumSize(1000, 700)
        self.setWindowTitle('Webshark')
        self.show()

    def create_menu(self):
        menu_bar = self.menuBar()
        file_menu: QMenu = menu_bar.addMenu(self.tr('&File'))

        connect_act = QAction(self.tr('&Certificate'), self)
        connect_act.triggered.connect(self.open_ca_dialog)
        file_menu.addAction(connect_act)

    def open_ca_dialog(self):
        pass


