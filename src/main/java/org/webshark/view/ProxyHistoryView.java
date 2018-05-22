package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.Initializable;
import org.webshark.viewmodel.ProxyHistoryViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class ProxyHistoryView implements FxmlView<ProxyHistoryViewModel>, Initializable {
    @InjectViewModel
    private ProxyHistoryViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
