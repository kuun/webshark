package org.webshark.model;

import io.netty.handler.codec.http.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HttpRecord {
    private IntegerProperty id = new SimpleIntegerProperty();
    private BooleanProperty completed = new SimpleBooleanProperty(false);
    private StringProperty method = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private IntegerProperty statusCode = new SimpleIntegerProperty();
    private HttpRequest req;
    private HttpResponse res;
    private List<HttpContent> reqContents;
    private List<HttpContent> resContents;
    private ProxyConf proxyConf;

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

    public int getStatusCode() {
        return statusCode.get();
    }

    public IntegerProperty statusCodeProperty() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode.set(statusCode);
    }

    public ProxyConf getProxyConf() {
        return proxyConf;
    }

    public HttpRecord setProxyConf(ProxyConf proxyConf) {
        this.proxyConf = proxyConf;
        return this;
    }

    public HttpRequest getReq() {
        return req;
    }

    public HttpRecord setReq(HttpRequest req) {
        this.req = req;
        setMethod(req.method().name());
        setUrl(req.uri());
        return this;
    }

    public HttpResponse getRes() {
        return res;
    }

    public HttpRecord setRes(HttpResponse res) {
        this.res = res;
        setStatusCode(res.status().code());
        return this;
    }

    public List<HttpContent> getReqContents() {
        return reqContents;
    }

    public List<HttpContent> getResContents() {
        return resContents;
    }

    public void addReqContent(HttpContent content) {
        if (reqContents == null) {
            if (content != LastHttpContent.EMPTY_LAST_CONTENT) {
                reqContents = new LinkedList<>();
            }
        }
        if (reqContents != null) {
            reqContents.add(content);
        }
    }

    public void addResContent(HttpContent content) {
        if (resContents == null) {
            if (content != LastHttpContent.EMPTY_LAST_CONTENT) {
                resContents = new LinkedList<>();
            }
        }
        if (resContents != null) {
            resContents.add(content);
        }
    }

    public ObservableList<HeaderInfo> getGeneralHeaderInfo() {
        ObservableList<HeaderInfo> infos = FXCollections.observableArrayList();

        var info = new HeaderInfo();
        info.setFieldName("Request Method");
        info.setFieldValue(req.method().name());
        infos.add(info);

        info = new HeaderInfo();
        info.setFieldName("Request URL");
        info.setFieldValue(req.uri());
        infos.add(info);

        if (res != null) {
            info = new HeaderInfo();
            info.setFieldName("Status Code");
            info.setFieldValue(res.status().toString());
            infos.add(info);
        }

        info = new HeaderInfo();
        info.setFieldName("Proxy Server");
        info.setFieldValue(proxyConf.getProxyAddr());
        infos.add(info);

        info = new HeaderInfo();
        info.setFieldName("Target Server");
        info.setFieldValue(proxyConf.getTargetAddr());
        infos.add(info);

        return infos;
    }

    public ObservableList<HeaderInfo> getRequestHeaderInfo() {
        ObservableList<HeaderInfo> infos = FXCollections.observableArrayList();

        collectHeaderInfo(req.headers(), infos);
        return infos;
    }

    public ObservableList<HeaderInfo> getResponseHeaderInfo() {
        ObservableList<HeaderInfo> infos = FXCollections.observableArrayList();

        if (res != null) {
            collectHeaderInfo(res.headers(), infos);
        }
        return infos;
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
