#!/usr/bin/python3
import logging
import sys

from PyQt5.QtWidgets import QApplication

from ui.main_window import MainWindow

if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    app = QApplication(sys.argv)
    w = MainWindow()

    sys.exit(app.exec_())