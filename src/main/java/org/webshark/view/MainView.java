package org.webshark.view;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.JavaView;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.webshark.viewmodel.MainViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class MainView extends AnchorPane implements JavaView<MainViewModel>, Initializable {
    @Inject
    private NotificationCenter notificationCenter;

    public enum Notification {
        PROXY_STARTED,
        RECORD_CLOSED,
    }

    public MainView() {
        setMinWidth(1200);
        setMinHeight(700);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        var viewTuple = FluentViewLoader.fxmlView(StartView.class).load();
        setNewNode(viewTuple.getView());

        notificationCenter.subscribe(Notification.PROXY_STARTED.name(), (key, payload) -> {
            var tuple = FluentViewLoader.fxmlView(RecordView.class).load();
            setNewNode(tuple.getView());
        });

        notificationCenter.subscribe(Notification.RECORD_CLOSED.name(), (key, payload) -> {
            var tuple = FluentViewLoader.fxmlView(ProxyStartView.class).load();
            setNewNode(tuple.getView());
        });
    }

    private void setNewNode(Node node) {
        setTopAnchor(node, 0.0);
        setRightAnchor(node, 0.0);
        setBottomAnchor(node, 0.0);
        setLeftAnchor(node, 0.0);
        getChildren().removeAll();
        getChildren().add(node);
    }
}
