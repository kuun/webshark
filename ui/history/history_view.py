from PyQt5.QtWidgets import QWidget, QVBoxLayout, QTableView

from model.history_model import HistoryModel


class HistoryView(QWidget):
    def __init__(self, parrent):
        super(HistoryView, self).__init__(parrent)
        self.__init_ui()

    def __init_ui(self):
        vbox = QVBoxLayout()

        self.table = QTableView()
        model = HistoryModel()
        self.table.setModel(model)

        vbox.addWidget(self.table)
        self.setLayout(vbox)