package org.webshark.servie;

import com.google.inject.ImplementedBy;
import org.webshark.model.ProxyConf;
import org.webshark.servie.impl.ProxyServiceImpl;

@ImplementedBy(ProxyServiceImpl.class)
public interface IProxyService extends IService {
    void startProxy(ProxyConf proxyConf);
}
