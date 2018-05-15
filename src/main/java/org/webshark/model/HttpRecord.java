package org.webshark.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HttpRecord {
    private IntegerProperty id = new SimpleIntegerProperty();
    private BooleanProperty completed = new SimpleBooleanProperty(false);
    private Request req;
    private Response res = new Response();
    private Content reqContent;
    private Content resContent;
    private ProxyConf proxyConf;
    private SimpleListProperty<HeaderInfo> generalInfo;

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }


    public StringProperty methodProperty() {
        return req.methodProperty();
    }

    public StringProperty urlProperty() {
        return req.urlProperty();
    }

    public IntegerProperty statusCodeProperty() {
        return res.statusCodeProperty();
    }

    public ProxyConf getProxyConf() {
        return proxyConf;
    }

    public HttpRecord setProxyConf(ProxyConf proxyConf) {
        this.proxyConf = proxyConf;
        return this;
    }

    public Request getReq() {
        return req;
    }

    public HttpRecord setReq(Request req) {
        this.req = req;
        return this;
    }

    public Response getRes() {
        return res;
    }

    public HttpRecord setRes(Response res) {
        this.res.copy(res);
        if (generalInfo != null) {
            for (var header : generalInfo) {
                if (header.getFieldName().equals("Status Code")) {
                    header.setFieldValue(Integer.toString(res.getStatusCode()));
                }
            }
        }
        return this;
    }

    public SimpleListProperty<HeaderInfo> generalInfoProperty() {
        generalInfo = new SimpleListProperty<>(FXCollections.observableArrayList());

        var info = new HeaderInfo();
        info.setFieldName("Request Method");
        info.setFieldValue(req.getMethod());
        generalInfo.add(info);

        info = new HeaderInfo();
        info.setFieldName("Request URL");
        info.setFieldValue(req.getUrl());
        generalInfo.add(info);

        if (res != null) {
            info = new HeaderInfo();
            info.setFieldName("Status Code");
            info.setFieldValue(Integer.toString(res.getStatusCode()));
            generalInfo.add(info);
        }

        info = new HeaderInfo();
        info.setFieldName("Proxy Server");
        info.setFieldValue(proxyConf.getProxyAddr());
        generalInfo.add(info);

        info = new HeaderInfo();
        info.setFieldName("Target Server");
        info.setFieldValue(proxyConf.getTargetAddr());
        generalInfo.add(info);
        return generalInfo;
    }

    public ListProperty<HeaderInfo> requestInfoProperty() {
        return req.headersProperty();
    }

    public ListProperty<HeaderInfo> responseInfoProperty() {
        return res.headersProperty();
    }

    public void cleanInfo() {
        generalInfo = null;
    }

    public void addReqContentBuffer(ByteBuf buf) {
        if (reqContent == null) {
            reqContent = new Content();
        }
        reqContent.addBuffer(buf);
    }

    public void addResContentBuffer(ByteBuf buf) {
        if (resContent == null) {
            resContent = new Content();
        }
        resContent.addBuffer(buf);
    }

    private void collectHeaderInfo(HttpHeaders headers, ObservableList<HeaderInfo> headerInfos) {
        for (var header : headers) {
            var info = new HeaderInfo();
            info.setFieldName(header.getKey());
            info.setFieldValue(header.getValue());
            headerInfos.add(info);
        }
    }
}
