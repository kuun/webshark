import datetime

from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import QDialog, QFormLayout, QLabel, QLineEdit
from cryptography.x509 import Certificate, Name, NameOID, NameAttribute

from container import Container
from service.ca_service import CAService


def get_attr_value(subject: Name, oid):
    attrs = subject.get_attributes_for_oid(oid)
    if len(attrs) > 0:
        attr: NameAttribute = attrs[0]
        return attr.value
    return ''


class CADialog(QDialog):
    def __init__(self, parent):
        super(CADialog, self).__init__(parent)
        self.ca_service: CAService = Container.ca_service()
        self.__init_ui()

    def __init_ui(self):
        self.setWindowModality(Qt.ApplicationModal)
        self.setModal(True)

        cert: Certificate = self.ca_service.get_ca_cert()
        subject: Name = cert.subject

        form = QFormLayout()

        form.addRow(self.tr('Country:'),
                    QLineEdit(get_attr_value(subject, NameOID.COUNTRY_NAME)))
        form.addRow(self.tr('State or Province:'),
                    QLineEdit(get_attr_value(subject, NameOID.STATE_OR_PROVINCE_NAME)))
        form.addRow(self.tr('City:'),
                    QLineEdit(get_attr_value(subject, NameOID.LOCALITY_NAME)))
        form.addRow(self.tr('Organization:'),
                    QLineEdit(get_attr_value(subject, NameOID.ORGANIZATION_NAME)))
        form.addRow(self.tr('Common name:'),
                    QLineEdit(get_attr_value(subject, NameOID.COMMON_NAME)))
        valid_info = '{} ~ {}'.format(cert.not_valid_before, cert.not_valid_after)
        form.addRow(self.tr('Valid duration:'), QLabel(valid_info))

        self.setLayout(form)
