package org.webshark.servie.proxy;

import com.google.inject.Singleton;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.internal.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.ProxyConf;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ProxyServiceImpl implements IProxyService {
    private final static Logger log = LoggerFactory.getLogger(ProxyServiceImpl.class);
    private EventLoopGroup eventLoopGroup;
    private Map<ProxyConf, ProxyServer> proxyServerMap = new HashMap<>();

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
