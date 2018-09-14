package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
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
    private Node reqBodyView;
    @FXML
    private BodyView reqBodyViewController;
    @FXML
    private Node resBodyView;
    @FXML
    private BodyView resBodyViewController;

    @InjectViewModel
    private RecordViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recordTable.setItems(viewModel.getRecords());
        colId.setCellValueFactory((record) -> record.getValue().idProperty().asObject());
        colMethod.setCellValueFactory((record) -> record.getValue().methodProperty());
        colUrl.setCellValueFactory((record) -> record.getValue().urlProperty());
        colStatus.setCellValueFactory((record) -> record.getValue().statusCodeProperty().asObject());
        colType.setCellValueFactory((record) -> {
            var contentTypeFull = record.getValue().getRes().contentTypeProperty();
            var contentType = new SimpleStringProperty();
            contentType.bindBidirectional(contentTypeFull, new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    if (object == null) return null;
                    int end = object.indexOf(';');
                    if (end == -1) {
                        return object;
                    } else {
                        return object.substring(0, end);
                    }
                }

                @Override
                public String fromString(String string) {
                    return null;
                }
            });
            return contentType;
        });

        recordTable.getFocusModel().focusedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.onFocusRecord(oldValue, newValue);
            }
        }));
        reqBodyViewController.setRequest(true);
        resBodyViewController.setRequest(false);
    }

}
