package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.webshark.model.HeaderInfo;
import org.webshark.viewmodel.DetailViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailView implements FxmlView<DetailViewModel>, Initializable {
    @FXML
    private TreeTableView<HeaderInfo> headerInfoTree;
    @FXML
    private TreeTableColumn<HeaderInfo, String> colHeaderName;
    @FXML
    private TreeTableColumn<HeaderInfo, String> colHeaderValue;

    @InjectViewModel
    private DetailViewModel viewModel;

    private static class DetailChangeListener implements ChangeListener<ObservableList<HeaderInfo>> {
        private TreeItem<HeaderInfo> node;

        public DetailChangeListener(TreeItem<HeaderInfo> node) {
            this.node = node;
        }

        @Override
        public void changed(ObservableValue<? extends ObservableList<HeaderInfo>> observable, ObservableList<HeaderInfo> oldValue, ObservableList<HeaderInfo> newValue) {
            var children = node.getChildren();
            children.clear();
            for (HeaderInfo headerInfo : newValue) {
                TreeItem<HeaderInfo> item = new TreeItem<>(headerInfo);
                children.add(item);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colHeaderName.setCellValueFactory((info) -> info.getValue().getValue().fieldNameProperty());
        colHeaderValue.setCellValueFactory((info) -> info.getValue().getValue().fieldValueProperty());
        TreeItem<HeaderInfo> root = new TreeItem<>();
        TreeItem<HeaderInfo> generalHeaders = new TreeItem<>();
        TreeItem<HeaderInfo> requestHeaders = new TreeItem<>();
        TreeItem<HeaderInfo> responseHeaders = new TreeItem<>();
        HeaderInfo info = new HeaderInfo();
        info.setFieldName("General Headers");
        generalHeaders.setValue(info);
        generalHeaders.setExpanded(true);
        info = new HeaderInfo();
        info.setFieldName("Request Headers");
        requestHeaders.setValue(info);
        requestHeaders.setExpanded(true);
        info = new HeaderInfo();
        info.setFieldName("Response Headers");
        responseHeaders.setValue(info);
        responseHeaders.setExpanded(true);
        root.getChildren().addAll(generalHeaders, requestHeaders, responseHeaders);

        headerInfoTree.setShowRoot(false);
        headerInfoTree.setRoot(root);

        viewModel.generalHeadersProperty().addListener(new DetailChangeListener(generalHeaders));
        viewModel.requestHeadersProperty().addListener(new DetailChangeListener(requestHeaders));
        viewModel.responseHeadersProperty().addListener(new DetailChangeListener(responseHeaders));
    }
}
