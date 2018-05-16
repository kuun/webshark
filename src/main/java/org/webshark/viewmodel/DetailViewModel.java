package org.webshark.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import org.webshark.model.HeaderInfo;
import org.webshark.model.HttpRecord;
import org.webshark.scope.RecordPageScope;

public class DetailViewModel implements ViewModel {
    @InjectScope
    private RecordPageScope scope;

    private SimpleListProperty<HeaderInfo> generalHeaders = new SimpleListProperty<>();
    private SimpleListProperty<HeaderInfo> requestHeaders = new SimpleListProperty<>();
    private SimpleListProperty<HeaderInfo> responseHeaders = new SimpleListProperty<>();

    public void initialize() {
        scope.subscribe(RecordPageScope.Notification.FOCUSED_RECORD_CHANGED.name(), (key, payload) -> {
            var oldRecord = (HttpRecord) payload[0];
            var newRecord = (HttpRecord)payload[1];
            if (oldRecord != null) {
                oldRecord.cleanInfo();
            }
            generalHeaders.bind(newRecord.generalInfoProperty());
            requestHeaders.bind(newRecord.requestInfoProperty());
            responseHeaders.bind(newRecord.responseInfoProperty());
        });
    }

    public ObservableList<HeaderInfo> getGeneralHeaders() {
        return generalHeaders.get();
    }

    public SimpleListProperty<HeaderInfo> generalHeadersProperty() {
        return generalHeaders;
    }

    public ObservableList<HeaderInfo> getRequestHeaders() {
        return requestHeaders.get();
    }

    public SimpleListProperty<HeaderInfo> requestHeadersProperty() {
        return requestHeaders;
    }

    public ObservableList<HeaderInfo> getResponseHeaders() {
        return responseHeaders.get();
    }

    public SimpleListProperty<HeaderInfo> responseHeadersProperty() {
        return responseHeaders;
    }
}
