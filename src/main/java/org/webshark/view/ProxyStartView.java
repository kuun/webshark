package org.webshark.view;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
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
    @Inject
    private NotificationCenter notificationCenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var conf = viewModel.getConf();
        proxyAddr.textProperty().bindBidirectional(conf.proxyAddrProperty());
        targetAddr.textProperty().bindBidirectional(conf.targetAddrProperty());
    }

    @FXML
    private void onStart() {
        try {
            viewModel.startProxy();
            notificationCenter.publish(MainView.Notification.PROXY_STARTED.name());
        } catch (MalformedURLException e) {
            var alert = new Alert(Alert.AlertType.ERROR, "malformed url", ButtonType.CLOSE);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
        }
    }
}
