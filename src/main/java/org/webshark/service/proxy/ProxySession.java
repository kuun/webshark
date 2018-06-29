package org.webshark.service.proxy;

import com.google.inject.Inject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.ProxyConf;
import org.webshark.service.record.IRecordService;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class ProxySession extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(ProxySession.class);
    private Bootstrap clientBootstrap;
    private ProxyConf proxyConf;
    private Channel proxyChannel;
    private TargetWriteListener targetWriteListener = new TargetWriteListener();
    private ProxyWriteListener proxyWriteListener = new ProxyWriteListener();
    @Inject
    private IRecordService recordService;
    private int currentRecordId;
    private Map<String, Channel> targetChannels = new HashMap<>();
    private Channel currentTargetChannel = null;
    private String currentTargetHost = null;


    // reads message from target server, then writes to the client.
    private class TargetChannelHandler extends SimpleChannelInboundHandler<HttpObject> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            // log.debug("response message: {}", msg);
            if (msg instanceof HttpResponse) {
                proxyChannel.writeAndFlush(msg).addListener(proxyWriteListener);
                recordService.recordResponse(currentRecordId, (HttpResponse)msg);
            } else if (msg instanceof HttpContent) {
                if (msg != LastHttpContent.EMPTY_LAST_CONTENT) {
                    ((HttpContent)msg).retain();
                    proxyChannel.writeAndFlush(msg).addListener(proxyWriteListener);
                }
                recordService.recordResponseContent(currentRecordId, (HttpContent)msg);
            } else {
                log.error("unsupported http message: {}", msg);
            }

        }
    }

    private class TargetWriteListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                proxyChannel.read();
            } else {
                log.error("failed to write target channel, target host: {}, error: {}",
                    currentTargetHost, ThrowableUtil.stackTraceToString(future.cause()));
                currentTargetChannel.close();
                proxyChannel.close();
            }
        }
    }

    private class ProxyWriteListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                currentTargetChannel.read();
            } else {
                log.error("failed to write proxy channel, client: {}, error: {}",
                    proxyChannel.remoteAddress(), ThrowableUtil.stackTraceToString(future.cause()));
                proxyChannel.close();
                currentTargetChannel.close();
            }
        }
    }

    public ProxySession setClientBootstrap(Bootstrap clientBootstrap) {
        this.clientBootstrap = clientBootstrap;
        return this;
    }

    public ProxySession setProxyConf(ProxyConf proxyConf) {
        this.proxyConf = proxyConf;
        return this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        proxyChannel = ctx.channel();
        proxyChannel.read();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel: {}, error: {}", ctx.channel(), ThrowableUtil.stackTraceToString(cause));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // log.debug("request message: {}", msg);
        if (msg instanceof HttpRequest) {
            // todo: record message
            var req = (HttpRequest)msg;
            var uri = req.uri();
            var url = new URL(uri);
            req.setUri(url.getPath());
            var host = req.headers().get("Host");
            currentTargetHost = host;
            var targetChannel = targetChannels.get(host);
            currentRecordId = recordService.recordRequest(proxyConf, req);
            if (targetChannel == null) {
                connectTargret(req, host);
                return;
            }
            if (!targetChannel.isActive()) {
                targetChannels.remove(host);
                connectTargret(req, host);
                return;
            }
            currentTargetChannel = targetChannel;
            currentTargetChannel.writeAndFlush(req).addListener(targetWriteListener);
        }else if (msg instanceof HttpContent){
            if (msg != LastHttpContent.EMPTY_LAST_CONTENT) {
                ((HttpContent)msg).retain();
                currentTargetChannel.writeAndFlush(msg).addListener(targetWriteListener);
            }
            recordService.recordRequestContent(currentRecordId, (HttpContent)msg);
        } else {
            log.error("unsuppored http message: {}", msg);
        }
    }

    private void connectTargret(HttpRequest req, String host) {
        var targetAddr = parseTargetAddr(host);
        var channelFuture = clientBootstrap.connect(targetAddr).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    log.debug("connected to target host: {}", host);
                    currentTargetChannel.pipeline().addLast(new TargetChannelHandler());
                    // send request message to target server
                    currentTargetChannel.writeAndFlush(req).addListener(targetWriteListener);
                    currentTargetChannel.read();
                } else {
                    log.debug("failed to connnect to target server: {}, error: {}",
                        host, ThrowableUtil.stackTraceToString(future.cause()));
                    var res = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_GATEWAY);
                    proxyChannel.writeAndFlush(res);
                    proxyChannel.close();
                    currentTargetChannel.close();
                    currentTargetChannel = null;
                }
            }
        });
        currentTargetChannel = channelFuture.channel();
        targetChannels.put(host, currentTargetChannel);
    }

    private SocketAddress parseTargetAddr(String host) {
        String targetHost;
        int targetPort;
        var tmp = host.split(":");
        targetHost = tmp[0];
        if (tmp.length == 1) {
            targetPort = 80;
        } else {
            targetPort = Integer.parseInt(tmp[1]);
        }
        return new InetSocketAddress(targetHost, targetPort);
    }
}
