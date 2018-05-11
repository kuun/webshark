package org.webshark.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.ProxyConf;
import org.webshark.service.record.IRecordService;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@Singleton
class ProxyServiceImpl implements IProxyService {
    private final static Logger log = LoggerFactory.getLogger(ProxyServiceImpl.class);
    private EventLoopGroup eventLoopGroup;
    private Map<ProxyConf, ProxyServer> proxyServerMap = new HashMap<>();
    @Inject
    private IRecordService recordService;

    public ProxyServiceImpl() {
        eventLoopGroup = new NioEventLoopGroup();
    }

    @Override
    public void startProxy(ProxyConf proxyConf) throws MalformedURLException {
        log.debug("start proxy: {}", proxyConf);
        var proxyServer = new ProxyServer(proxyConf, eventLoopGroup);
        proxyServer.start();
        proxyServerMap.put(proxyConf, proxyServer);
    }

    @Override
    public void stopPorxy(ProxyConf proxyConf) {
        var proxyServer = proxyServerMap.get(proxyConf);
        if (proxyServer != null) {
            proxyServer.stop();
        }
    }
}
