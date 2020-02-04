#!/usr/bin/python3
import logging
import sys

from PyQt5.QtWidgets import QApplication

from ui.main_window import MainWindow

from container import Container


# FORMAT = '%(asctime)s %(levelname)s %(module)s:%(lineno)d: %(message)s'
# logging.basicConfig(format=FORMAT)


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)

    config_service = Container.config_service()
    config_service.create_config_dir()

    app = QApplication(sys.argv)
    w = MainWindow()
    sys.exit(app.exec_())