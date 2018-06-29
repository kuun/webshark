package org.webshark.service.proxy;

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
import org.webshark.App;
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

    public void start() {
        var laddr = new InetSocketAddress(conf.getProxyAddr(), conf.getProxyPort());

        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                var pipeline = ch.pipeline();

                pipeline.addLast(
                    new HttpRequestDecoder(),
                    new HttpResponseEncoder()
                );
                // add proxy session to channel pipeline.
                var session = App.injector.getInstance(ProxySession.class);
                session.setClientBootstrap(clientBootstrap)
                    .setProxyConf(conf);
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
