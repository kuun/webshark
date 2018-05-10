package org.webshark.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class ProxyConf {
    private SimpleStringProperty proxyAddr = new SimpleStringProperty();
    private SimpleStringProperty targetAddr = new SimpleStringProperty();

    public String getProxyAddr() {
        return proxyAddr.get();
    }

    public void setProxyAddr(String proxyAddr) {
        this.proxyAddr.set(proxyAddr);
    }

    public SimpleStringProperty proxyAddrProperty() {
        return proxyAddr;
    }

    public String getTargetAddr() {
        return targetAddr.get();
    }

    public void setTargetAddr(String targetAddr) {
        this.targetAddr.set(targetAddr);
    }

    public SimpleStringProperty targetAddrProperty() {
        return targetAddr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyConf proxyConf = (ProxyConf) o;
        return Objects.equals(proxyAddr, proxyConf.proxyAddr) &&
            Objects.equals(targetAddr, proxyConf.targetAddr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxyAddr, targetAddr);
    }

    @Override
    public String toString() {
        return "ProxyConf{" +
            "proxyAddr='" + proxyAddr + '\'' +
            ", targetAddr='" + targetAddr + '\'' +
            '}';
    }
}
