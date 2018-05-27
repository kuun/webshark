package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.webshark.model.ProxyConf;
import org.webshark.viewmodel.ProxyHistoryViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class ProxyHistoryView implements FxmlView<ProxyHistoryViewModel>, Initializable {
    @FXML
    private TableView<ProxyConf> historyTable;
    @FXML
    private TableColumn<ProxyConf, String> colProxyAddr;
    @FXML
    private TableColumn<ProxyConf, String> colTargetAddr;

    @InjectViewModel
    private ProxyHistoryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        historyTable.itemsProperty().bind(viewModel.confsProperty());
        colProxyAddr.setCellValueFactory((item) -> item.getValue().proxyAddrProperty());
        colTargetAddr.setCellValueFactory((item) -> item.getValue().targetAddrProperty());
        historyTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        
                    }
                }
            }
        });
    }
}
