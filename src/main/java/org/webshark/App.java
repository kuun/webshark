package org.webshark;

import com.google.inject.Injector;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.webshark.view.MainView;

public class App extends MvvmfxGuiceApplication {
    public static Injector injector;

    @Override
    public void startMvvmfx(Stage stage) throws Exception {
        injector = getInjector();

        stage.setTitle("Webshark");
        stage.setMinWidth(1200);
        stage.setMinHeight(700);

        var viewTuple = FluentViewLoader.javaView(MainView.class).load();
        var root = viewTuple.getView();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
