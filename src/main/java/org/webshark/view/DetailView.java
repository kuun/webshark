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
import org.webshark.viewmodel.DetailViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailView implements FxmlView<DetailViewModel>, Initializable {
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
    private DetailViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        generalHeaderTable.itemsProperty().bind(viewModel.generalHeadersProperty());
        requestHeaderTable.itemsProperty().bind(viewModel.requestHeadersProperty());
        responseHeaderTable.itemsProperty().bind(viewModel.responseHeadersProperty());
        autoSizeTable(generalHeaderTable);
        autoSizeTable(requestHeaderTable);
        autoSizeTable(responseHeaderTable);
    }

    private void autoSizeTable(TableView table) {
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems())).add(26));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }
}
