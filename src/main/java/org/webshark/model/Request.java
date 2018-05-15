package org.webshark.model;

import com.cathive.fx.guice.FXMLController;
import io.netty.handler.codec.http.HttpRequest;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Request {
    private StringProperty method = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private ListProperty<HeaderInfo> headers = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Request(HttpRequest req) {
        method.set(req.method().name());
        url.set(req.uri());
        for (var header : req.headers()) {
            var info = new HeaderInfo();
            info.setFieldName(header.getKey());
            info.setFieldValue(header.getValue());
            headers.add(info);
        }
    }

    public String getMethod() {
        return method.get();
    }

    public StringProperty methodProperty() {
        return method;
    }

    public void setMethod(String method) {
        this.method.set(method);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public ObservableList<HeaderInfo> getHeaders() {
        return headers.get();
    }

    public ListProperty<HeaderInfo> headersProperty() {
        return headers;
    }

    public void setHeaders(ObservableList<HeaderInfo> headers) {
        this.headers.set(headers);
    }

    public void addHeader(HeaderInfo header) {
        headers.add(header);
    }

    public void addHeader(String headerName, String headerValue) {
        var header = new HeaderInfo();
        header.setFieldName(headerName);
        header.setFieldValue(headerValue);
        headers.add(header);
    }

    public void setHeader(String headerName, String headerValue) {
        for (var header : headers) {
            if (headerName.equalsIgnoreCase(header.getFieldName())) {
                header.setFieldValue(headerValue);
                return;
            }
        }
    }
}
