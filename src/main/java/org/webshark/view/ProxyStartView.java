package org.webshark.view;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.util.converter.IntegerStringConverter;
import org.webshark.viewmodel.ProxyStartViewModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ProxyStartView implements FxmlView<ProxyStartViewModel>, Initializable {
    @FXML
    private TextField proxyAddr;
    @FXML
    private Spinner<Integer> proxyPort;
    @InjectViewModel
    private ProxyStartViewModel viewModel;
    @Inject
    private NotificationCenter notificationCenter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var conf = viewModel.getConf();
        proxyAddr.textProperty().bindBidirectional(conf.proxyAddrProperty());
        NumberFormat format = NumberFormat.getIntegerInstance();
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                if (c.getControlNewText().equals("")) {
                    return c;
                }
                ParsePosition parsePosition = new ParsePosition(0);
                var number = format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 || parsePosition.getIndex() < c.getControlNewText().length()) {
                    return null;
                }
                var newValue = number.intValue();
                if (newValue < 1 || newValue > 65535) {
                    return null;
                }
            }
            return c;
        };
        TextFormatter<Integer> portFormater = new TextFormatter<Integer>(new IntegerStringConverter(), 8000, filter);
        proxyPort.getEditor().setTextFormatter(portFormater);;
        proxyPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, 8000));
        conf.proxyPortProperty().bind(proxyPort.valueProperty());
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
