package org.webshark.servie.record;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import org.webshark.model.ProxyConf;

import java.util.LinkedList;
import java.util.List;

public class HttpRecord {
    private int id;
    private boolean completed = false;
    private ProxyConf proxyConf;
    private HttpRequest req;
    private HttpResponse res;
    private List<HttpContent> reqContents;
    private List<HttpContent> resContents;

    public int getId() {
        return id;
    }

    public HttpRecord setId(int id) {
        this.id = id;
        return this;
    }

    public boolean isCompleted() {
        return completed;
    }

    public HttpRecord setCompleted(boolean completed) {
        this.completed = completed;
        return this;
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
        return this;
    }

    public HttpResponse getRes() {
        return res;
    }

    public HttpRecord setRes(HttpResponse res) {
        this.res = res;
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
}
