package org.webshark.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class ProxyConf {
    private SimpleStringProperty proxyAddr = new SimpleStringProperty("127.0.0.1");
    private SimpleIntegerProperty proxyPort = new SimpleIntegerProperty();

    public String getProxyAddr() {
        return proxyAddr.get();
    }

    public void setProxyAddr(String proxyAddr) {
        this.proxyAddr.set(proxyAddr);
    }

    public SimpleStringProperty proxyAddrProperty() {
        return proxyAddr;
    }

    public int getProxyPort() {
        return proxyPort.get();
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort.set(proxyPort);
    }

    public SimpleIntegerProperty proxyPortProperty() {
        return proxyPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyConf proxyConf = (ProxyConf) o;
        return Objects.equals(proxyAddr, proxyConf.proxyAddr) &&
            Objects.equals(proxyPort, proxyConf.proxyPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxyAddr, proxyPort);
    }

    @Override
    public String toString() {
        return "ProxyConf{" +
            "proxyAddr='" + proxyAddr + '\'' +
            ", proxyPort='" + proxyPort + '\'' +
            '}';
    }
}
