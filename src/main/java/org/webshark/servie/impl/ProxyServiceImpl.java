package org.webshark.servie.impl;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.ProxyConf;
import org.webshark.servie.IProxyService;

@Singleton
public class ProxyServiceImpl implements IProxyService {
    private final static Logger log = LoggerFactory.getLogger(ProxyServiceImpl.class);

    @Override
    public void startProxy(ProxyConf proxyConf) {
        log.debug("{}", proxyConf);
    }

    @Override
    public void init() {

    }
}
