package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import org.webshark.model.HeaderInfo;
import org.webshark.viewmodel.DetailViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailView implements FxmlView<DetailViewModel>, Initializable {
    @FXML
    private ListView<HeaderInfo> generalHeaderList;
    @FXML
    private TableView requestHeaderTable, responseHeaderTable;
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

    private class InfoChangeListener implements ChangeListener {
        private ListView table;

        public InfoChangeListener(ListView table) {
            this.table = table;
        }

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            var height = table.fixedCellSizeProperty().get() * table.getItems().size() + 20;
            if (height == 0) {
                height = 24;
            }
            table.setPrefHeight(height);
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

    private static class HeaderInfoCell extends ListCell<HeaderInfo> {
        static final Font boldFont = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize());
        @Override
        protected void updateItem(HeaderInfo item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                var pane = new StackPane();
                TextFlow textFlow = new TextFlow();
                textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
                var nameText = new Label(item.getFieldName() + " : ");
                nameText.setFont(boldFont);
                var valueText = new Label(item.getFieldValue());
                valueText.setEllipsisString("...");
                valueText.setPrefWidth(Region.USE_PREF_SIZE);
                textFlow.getChildren().addAll(nameText, valueText);
                // pane.getChildren().add(textFlow);
                setGraphic(textFlow);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Callback<TableColumn.CellDataFeatures<HeaderInfo, String>, ObservableValue<String>> fieldNameCellFactory =
            (info) -> info.getValue().fieldNameProperty();
        Callback<TableColumn.CellDataFeatures<HeaderInfo, String>, ObservableValue<String>> filedValueCellFactory =
            (info) -> info.getValue().fieldValueProperty();
        var cellFactory = new CellFactory();

//        generalColFieldName.setCellValueFactory(fieldNameCellFactory);
//        generalColFieldName.setCellFactory(cellFactory);
//        generalColFieldValue.setCellValueFactory(filedValueCellFactory);
//        generalColFieldValue.setCellFactory(cellFactory);
        requestColFieldName.setCellValueFactory(fieldNameCellFactory);
        requestColFieldName.setCellFactory(cellFactory);
        requestColFieldValue.setCellValueFactory(filedValueCellFactory);
        requestColFieldValue.setCellFactory(cellFactory);
        responseColFieldName.setCellValueFactory(fieldNameCellFactory);
        responseColFieldName.setCellFactory(cellFactory);
        responseColFieldValue.setCellValueFactory(filedValueCellFactory);
        responseColFieldValue.setCellFactory(cellFactory);

        generalHeaderList.itemsProperty().bind(viewModel.generalHeadersProperty());
        generalHeaderList.setCellFactory((list) -> new HeaderInfoCell());
        generalHeaderList.itemsProperty().addListener(new InfoChangeListener(generalHeaderList));
//        generalHeaderList.itemsProperty().addListener(new DetailChangeListener(generalHeaderList));
        requestHeaderTable.itemsProperty().bind(viewModel.requestHeadersProperty());
        requestHeaderTable.itemsProperty().addListener(new DetailChangeListener(requestHeaderTable));
        responseHeaderTable.itemsProperty().bind(viewModel.responseHeadersProperty());
        responseHeaderTable.itemsProperty().addListener(new DetailChangeListener(responseHeaderTable));

        generalAccordion.setExpandedPane(generalPane);
        requestAccordion.setExpandedPane(requestPane);
        responseAccordion.setExpandedPane(responsePane);
    }
}
