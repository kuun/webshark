package org.webshark.servie.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.internal.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.ProxyConf;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;

class ProxyServer {
    private final static Logger log = LoggerFactory.getLogger(ProxyServer.class);
    private ProxyConf conf;
    private ServerBootstrap serverBootstrap;
    private Bootstrap clientBootstrap;
    private ChannelFuture channelFuture;
    private SocketAddress targetAddr;
    private String targetHost;

    public ProxyServer(ProxyConf conf, EventLoopGroup eventLoopGroup) {
        this.conf = conf;
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.AUTO_READ, true)
            .childOption(ChannelOption.AUTO_READ, false);

        clientBootstrap = new Bootstrap();
        clientBootstrap.group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.AUTO_READ, false);
    }

    public ProxyConf getConf() {
        return conf;
    }

    public void start() throws MalformedURLException {
        var proxyUrl = new URL(conf.getProxyAddr());
        var proto = proxyUrl.getProtocol();
        var proxyPort = proxyUrl.getPort();
        if (proxyPort == -1) {
            proxyPort = proxyUrl.getDefaultPort();
        }
        var laddr = new InetSocketAddress(proxyUrl.getHost(), proxyPort);

        var targetUrl = new URL(conf.getTargetAddr());
        var targetPort = targetUrl.getPort();
        if (targetPort == -1) {
            targetPort = targetUrl.getDefaultPort();
        }
        targetAddr = new InetSocketAddress(targetUrl.getHost(), targetPort);
        if (targetPort == 80) {
            targetHost = targetUrl.getHost();
        } else {
            targetHost = targetUrl.getHost() + ":" + targetPort;
        }

        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                var pipeline = ch.pipeline();

                if (proto.equalsIgnoreCase("http")) {
                    pipeline.addLast(
                        new HttpRequestDecoder(),
                        new HttpResponseEncoder()
                    );
                }
                // add proxy session to channel pipeline.
                var session = new ProxySession();
                session.setClientBootstrap(clientBootstrap)
                    .setTargetAddr(targetAddr)
                    .setTargetHost(targetHost);
                pipeline.addLast(session);
            }
        });

        clientBootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                var pipeline = ch.pipeline();
                pipeline.addLast(
                    new HttpRequestEncoder(),
                    new HttpResponseDecoder()
                );
            }
        });

        try {
            channelFuture = serverBootstrap.bind(laddr).sync();
            if (!channelFuture.isSuccess()) {
                log.error("failed to start server, error: {}",
                    ThrowableUtil.stackTraceToString(channelFuture.cause()));
            }
        } catch (InterruptedException e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }

    public void stop() {
        try {
            var future = channelFuture.channel().close().sync();
            if (!future.isSuccess()) {
                log.error("failed to stop proxy server, error: {}",
                    ThrowableUtil.stackTraceToString(future.cause()));
            }
        } catch (InterruptedException e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
    }
}
