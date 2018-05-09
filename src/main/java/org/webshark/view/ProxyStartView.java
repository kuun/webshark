package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import org.webshark.viewmodel.ProxyStartViewModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProxyStartView implements FxmlView<ProxyStartViewModel>, Initializable {
    @FXML
    private TextField proxyAddr;
    @FXML
    private TextField targetAddr;
    @InjectViewModel
    private ProxyStartViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        proxyAddr.textProperty().bindBidirectional(viewModel.proxyAddrProperty());
        targetAddr.textProperty().bindBidirectional(viewModel.targetAddrProperty());
    }

    @FXML
    private void onStart() {
        try {
            viewModel.startProxy();
        } catch (MalformedURLException e) {
            var alert = new Alert(Alert.AlertType.ERROR, "malformed url", ButtonType.CLOSE);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
        }
    }
}
