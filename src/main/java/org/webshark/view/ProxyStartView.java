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
    private TextField proxyPort;
    @FXML
    private TextField targetAddr;
    @FXML
    private TextField targetPort;
    @InjectViewModel
    private ProxyStartViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
