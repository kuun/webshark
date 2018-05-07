package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.webshark.viewmodel.ProxyStartViewModel;

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
        viewModel.startProxy();
    }
}
