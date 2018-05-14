package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
                viewModel.onFocusRecord(newValue);
            }
        }));
    }

}
