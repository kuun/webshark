package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.webshark.model.HeaderInfo;
import org.webshark.model.HttpRecord;
import org.webshark.viewmodel.RecordViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class RecordView implements FxmlView<RecordViewModel>, Initializable {
    @FXML
    private TableView<HttpRecord> recordTable;
    @FXML
    private TableColumn<HttpRecord, Integer> colId;
    @FXML
    private TableColumn<HttpRecord, String> colMethod;
    @FXML
    private TableColumn<HttpRecord, String> colUrl;
    @FXML
    private TableColumn<HttpRecord, Integer> colStatus;
    @FXML
    private TableColumn<HttpRecord, String> colType;
    @FXML
    private TableColumn<HttpRecord, Float> colTime;
    @FXML
    private TableView generalHeaderTable;
    @FXML
    private TableColumn<HeaderInfo, String> generalColFieldName;
    @FXML
    private TableColumn<HeaderInfo, String> generalColFieldValue;
    @FXML
    private TableView requestHeaderTable;
    @FXML
    private TableColumn<HeaderInfo, String> requestColFieldName;
    @FXML
    private TableColumn<HeaderInfo, String> requestColFieldValue;
    @FXML
    private TableView responseHeaderTable;
    @FXML
    private TableColumn<HeaderInfo, String> responseColFieldName;
    @FXML
    private TableColumn<HeaderInfo, String> responseColFieldValue;
    @InjectViewModel
    private RecordViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recordTable.setItems(viewModel.getRecords());
        colId.setCellValueFactory((record) -> record.getValue().idProperty().asObject());
        colMethod.setCellValueFactory((record) -> record.getValue().methodProperty());
        colUrl.setCellValueFactory((record) -> record.getValue().urlProperty());
        colStatus.setCellValueFactory((record) -> record.getValue().statusCodeProperty().asObject());

        recordTable.getFocusModel().focusedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                var record = newValue;
                generalHeaderTable.setItems(record.getGeneralHeaderInfo());
                autoSizeTable(generalHeaderTable);
                requestHeaderTable.setItems(record.getRequestHeaderInfo());
                autoSizeTable(requestHeaderTable);
                responseHeaderTable.setItems(record.getResponseHeaderInfo());
                autoSizeTable(responseHeaderTable);
            }
        }));

        Callback<TableColumn.CellDataFeatures<HeaderInfo, String>, ObservableValue<String>> fieldNameCellFactory =
            (info) -> info.getValue().fieldNameProperty();
        Callback<TableColumn.CellDataFeatures<HeaderInfo, String>, ObservableValue<String>> filedValueCellFactory =
            (info) -> info.getValue().fieldValueProperty();
        generalColFieldName.setCellValueFactory(fieldNameCellFactory);
        generalColFieldValue.setCellValueFactory(filedValueCellFactory);
        requestColFieldName.setCellValueFactory(fieldNameCellFactory);
        requestColFieldValue.setCellValueFactory(filedValueCellFactory);
        responseColFieldName.setCellValueFactory(fieldNameCellFactory);
        responseColFieldValue.setCellValueFactory(filedValueCellFactory);

    }

    private void autoSizeTable(TableView table) {
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems())).add(26));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());

    }
}
