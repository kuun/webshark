package org.webshark.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Response {
    private IntegerProperty statusCode = new SimpleIntegerProperty();
    private StringProperty contentType = new SimpleStringProperty();
    private ListProperty<HeaderInfo> headers = new SimpleListProperty<>();
    private Content content = new Content();

    public Response() {
    }

    public Response(HttpResponse res) {
        headers.setValue(FXCollections.observableArrayList());
        statusCode.setValue(res.status().code());
        contentType.setValue(res.headers().get("Content-Type"));
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
        contentType.setValue(res.getContentType());
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

    public void addContentBuffer(ByteBuf buf) {
        content.addBuffer(buf);
    }

    public Content getContent() {
        return content;
    }
}
