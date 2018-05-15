package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.webshark.model.HeaderInfo;
import org.webshark.viewmodel.DetailViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailView implements FxmlView<DetailViewModel>, Initializable {
    @FXML
    private TableView generalHeaderTable, requestHeaderTable, responseHeaderTable;
    @FXML
    private TableColumn<HeaderInfo, String> generalColFieldName,  generalColFieldValue;
    @FXML
    private TableColumn<HeaderInfo, String> requestColFieldName,  requestColFieldValue;
    @FXML
    private TableColumn<HeaderInfo, String> responseColFieldName, responseColFieldValue;
    @FXML
    private Accordion generalAccordion, requestAccordion, responseAccordion;
    @FXML
    private TitledPane generalPane, requestPane, responsePane;

    @InjectViewModel
    private DetailViewModel viewModel;

    private class DetailChangeListener implements ChangeListener {
        private TableView table;

        public DetailChangeListener(TableView table) {
            this.table = table;
        }

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            table.layout();
        }
    }

    private static class CellFactory implements Callback<TableColumn<HeaderInfo, String>, TableCell<HeaderInfo, String>> {
        @Override
        public TableCell<HeaderInfo, String> call(TableColumn<HeaderInfo, String> param) {
            var cell = new TableCell<HeaderInfo, String>();
            var text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(cell.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Callback<TableColumn.CellDataFeatures<HeaderInfo, String>, ObservableValue<String>> fieldNameCellFactory =
            (info) -> info.getValue().fieldNameProperty();
        Callback<TableColumn.CellDataFeatures<HeaderInfo, String>, ObservableValue<String>> filedValueCellFactory =
            (info) -> info.getValue().fieldValueProperty();
        var cellFactory = new CellFactory();

        generalColFieldName.setCellValueFactory(fieldNameCellFactory);
        generalColFieldName.setCellFactory(cellFactory);
        generalColFieldValue.setCellValueFactory(filedValueCellFactory);
        generalColFieldValue.setCellFactory(cellFactory);
        requestColFieldName.setCellValueFactory(fieldNameCellFactory);
        requestColFieldName.setCellFactory(cellFactory);
        requestColFieldValue.setCellValueFactory(filedValueCellFactory);
        requestColFieldValue.setCellFactory(cellFactory);
        responseColFieldName.setCellValueFactory(fieldNameCellFactory);
        responseColFieldName.setCellFactory(cellFactory);
        responseColFieldValue.setCellValueFactory(filedValueCellFactory);
        responseColFieldValue.setCellFactory(cellFactory);

        generalHeaderTable.itemsProperty().bind(viewModel.generalHeadersProperty());
        generalHeaderTable.itemsProperty().addListener(new DetailChangeListener(generalHeaderTable));
        requestHeaderTable.itemsProperty().bind(viewModel.requestHeadersProperty());
        requestHeaderTable.itemsProperty().addListener(new DetailChangeListener(requestHeaderTable));
        responseHeaderTable.itemsProperty().bind(viewModel.responseHeadersProperty());
        responseHeaderTable.itemsProperty().addListener(new DetailChangeListener(responseHeaderTable));

        generalAccordion.setExpandedPane(generalPane);
        requestAccordion.setExpandedPane(requestPane);
        responseAccordion.setExpandedPane(responsePane);
    }
}
