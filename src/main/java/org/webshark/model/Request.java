package org.webshark.model;

import io.netty.buffer.ByteBuf;
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
    private StringProperty host = new SimpleStringProperty();
    private StringProperty contentType = new SimpleStringProperty();
    private ListProperty<HeaderInfo> headers = new SimpleListProperty<>(FXCollections.observableArrayList());
    private Content content = new Content();

    public Request(HttpRequest req) {
        method.set(req.method().name());
        url.set(req.uri());
        host.set(req.headers().get("Host"));
        contentType.setValue(req.headers().get("Content-Type"));
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

    public String getHost() {
        return host.get();
    }

    public StringProperty hostProperty() {
        return host;
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public String getContentType() {
        return contentType.get();
    }

    public StringProperty contentTypeProperty() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType.set(contentType);
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

    public void addContentBuffer(ByteBuf buf) {
        content.addBuffer(buf);
    }

    public Content getContent() {
        return content;
    }
}
