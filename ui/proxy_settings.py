from PyQt5.QtWidgets import QWidget, QFormLayout, QLineEdit, QSpinBox, QVBoxLayout, QHBoxLayout

from container import Container
from ui.common.switch_button import SwitchButton


class ProxySettingsWidget(QWidget):
    def __init__(self, parrent):
        super(ProxySettingsWidget, self).__init__(parrent)
        self.laddr = '127.0.0.1'
        self.lport = 8080
        self.__init_ui()

    def __init_ui(self):
        vbox = QVBoxLayout()
        self.setLayout(vbox)

        hbox = QHBoxLayout()
        vbox.addLayout(hbox)

        form = QFormLayout()
        hbox.addLayout(form)
        hbox.addStretch(1)

        self.laddr_edit = QLineEdit(self.laddr, self)
        form.addRow(self.tr('Listen address: '), self.laddr_edit)
        self.lport_edit = QSpinBox(self)
        self.lport_edit.setRange(1, 65534)
        self.lport_edit.setValue(self.lport)
        form.addRow(self.tr('Listen port: '), self.lport_edit)

        self.switch_btn = SwitchButton(self)
        form.addRow(self.tr('Start proxy server: '), self.switch_btn)
        self.switch_btn.toggled.connect(self.toggle_proxy_server)

    def toggle_proxy_server(self):
        proxy_service = Container.proxy_service()
        if self.switch_btn.isChecked():
            self.laddr = self.laddr_edit.text()
            self.lport = self.lport_edit.value()
            proxy_service.start(self.laddr, self.lport)
        else:
            proxy_service.close()