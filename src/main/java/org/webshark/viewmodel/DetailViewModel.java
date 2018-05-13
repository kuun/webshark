package org.webshark.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import org.webshark.model.HeaderInfo;
import org.webshark.model.HttpRecord;
import org.webshark.scope.RecordPageScope;

@ScopeProvider(scopes = {RecordPageScope.class})
public class DetailViewModel implements ViewModel {
    @InjectScope
    private RecordPageScope scope;

    private SimpleListProperty<HeaderInfo> generalHeaders = new SimpleListProperty<>();
    private SimpleListProperty<HeaderInfo> requestHeaders = new SimpleListProperty<>();
    private SimpleListProperty<HeaderInfo> responseHeaders = new SimpleListProperty<>();


    public DetailViewModel() {
        scope.subscribe(RecordPageScope.Notification.FOCUSED_RECORD_CHANGED.name(), (key, payload) -> {
            var record = (HttpRecord)payload[0];

            generalHeaders.removeAll();
            generalHeaders.addAll(record.getGeneralHeaderInfo());

            generalHeaders.setValue(record.getGeneralHeaderInfo());
            requestHeaders.setValue(record.getRequestHeaderInfo());
            responseHeaders.setValue(record.getResponseHeaderInfo());
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
