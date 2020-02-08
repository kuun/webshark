import typing

from PyQt5.QtCore import QAbstractTableModel, QModelIndex, Qt, QSize, pyqtSignal

from model.http_session import HttpSession


class BaseColumn:
    def __init__(self, name):
        self.name = name

    def get_data(self, session: HttpSession):
        pass


class MethodColumn(BaseColumn):
    def get_data(self, session: HttpSession):
        return session.get_method()


class UrlColumn(BaseColumn):
    def get_data(self, session: HttpSession):
        return session.get_url()


class StatusCodeColumn(BaseColumn):
    def get_data(self, session: HttpSession):
        return session.get_status_code()


class ResponseContentTypeColumn(BaseColumn):
    def get_data(self, session: HttpSession):
        return session.get_response_content_type()


class HistoryModel(QAbstractTableModel):
    add_session = pyqtSignal(HttpSession)

    def __init__(self):
        super(HistoryModel, self).__init__()
        self.histories = []
        self.columns = [
            MethodColumn(self.tr('Method')),
            UrlColumn(self.tr('URL')),
            StatusCodeColumn(self.tr('Status code')),
            ResponseContentTypeColumn('Content type')
        ]

    def rowCount(self, parent: QModelIndex = ...) -> int:
        return len(self.histories)

    def columnCount(self, parent: QModelIndex = ...) -> int:
        return len(self.columns)

    def headerData(self, section: int, orientation: Qt.Orientation, role: int = ...) -> typing.Any:
        if orientation == Qt.Vertical:
            return super().headerData(section, orientation, role)
        column = self.columns[section]
        if role == Qt.DisplayRole:
            return column.name

    def data(self, index: QModelIndex, role: int = ...) -> typing.Any:
        if role == Qt.DisplayRole or role == Qt.ToolTipRole:
            column = self.columns[index.column()]
            record = self.histories[index.row()]
            return column.get_data(record)
