package org.webshark.viewmodel;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.oracle.tools.packager.IOUtils;
import de.saxsys.mvvmfx.ViewModel;
import io.netty.util.internal.ThrowableUtil;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.ProxyConf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

public class ProxyHistoryViewModel implements ViewModel {
    private static final Logger log = LoggerFactory.getLogger(ProxyHistoryViewModel.class);
    private static final String HISTORY_FILE = "../etc/history.json";
    private ListProperty<ProxyConf> confs = new SimpleListProperty<>(FXCollections.observableArrayList());



    public void initialize() {

    }

    public ObservableList<ProxyConf> getConfs() {
        return confs.get();
    }

    public ListProperty<ProxyConf> confsProperty() {
        return confs;
    }

    private void loadHistory() {
        try {
            FileInputStream file = new FileInputStream(HISTORY_FILE);
            JsonReader reader = new JsonReader(new InputStreamReader(file));
            var gson = new GsonBuilder().create();
            var type = new TypeToken<Collection<ProxyConf>>(){}.getType();
            Collection<ProxyConf> proxyConfs = gson.fromJson(reader, type);
            confs.addAll(proxyConfs);
        } catch (FileNotFoundException e) {
            log.error("can't find history file, error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }
}
