package org.webshark.model;

import io.netty.handler.codec.http.HttpResponse;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Response {
    private IntegerProperty statusCode = new SimpleIntegerProperty();
    private ListProperty<HeaderInfo> headers = new SimpleListProperty<>();

    public Response() {
    }

    public Response(HttpResponse res) {
        headers.setValue(FXCollections.observableArrayList());
        statusCode.setValue(res.status().code());
        for (var header : res.headers()) {
            var info = new HeaderInfo();
            info.setFieldName(header.getKey());
            info.setFieldValue(header.getValue());
            headers.add(info);
        }
    }

    public void copy(Response res) {
        headers.setValue(res.getHeaders());
        statusCode.setValue(res.getStatusCode());
    }

    public int getStatusCode() {
        return statusCode.get();
    }

    public IntegerProperty statusCodeProperty() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode.set(statusCode);
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
}
