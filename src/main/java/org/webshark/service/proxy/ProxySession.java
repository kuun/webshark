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

import java.net.HttpCookie;
import java.net.SocketAddress;

class ProxySession extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger log = LoggerFactory.getLogger(ProxySession.class);
    private Bootstrap clientBootstrap;
    private ProxyConf proxyConf;
    private SocketAddress targetAddr;
    private String targetHost;
    private Channel proxyChannel;
    private Channel targetChannel;
    private TargetWriteListener targetWriteListener = new TargetWriteListener();
    private ProxyWriteListener proxyWriteListener = new ProxyWriteListener();
    @Inject
    private IRecordService recordService;
    private int currentRecordId;


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
                    targetHost, ThrowableUtil.stackTraceToString(future.cause()));
                targetChannel.close();
                proxyChannel.close();
            }
        }
    }

    private class ProxyWriteListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                targetChannel.read();
            } else {
                log.error("failed to write proxy channel, client: {}, error: {}",
                    proxyChannel.remoteAddress(), ThrowableUtil.stackTraceToString(future.cause()));
                proxyChannel.close();
                targetChannel.close();
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

    public ProxySession setTargetAddr(SocketAddress targetAddr) {
        this.targetAddr = targetAddr;
        return this;
    }

    public ProxySession setTargetHost(String targetHost) {
        this.targetHost = targetHost;
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
            req.headers().set("Host", targetHost);
            connectTargret(req);
            currentRecordId = recordService.recordRequest(proxyConf, req);
        }else if (msg instanceof HttpContent){
            if (msg != LastHttpContent.EMPTY_LAST_CONTENT) {
                ((HttpContent)msg).retain();
                targetChannel.writeAndFlush(msg).addListener(targetWriteListener);
            }
            recordService.recordRequestContent(currentRecordId, (HttpContent)msg);
        } else {
            log.error("unsuppored http message: {}", msg);
        }
    }

    private void connectTargret(HttpRequest req) {
        var channelFuture = clientBootstrap.connect(targetAddr).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    log.debug("connected to target host: {}", targetHost);
                    targetChannel.pipeline().addLast(new TargetChannelHandler());
                    // send request message to target server
                    targetChannel.writeAndFlush(req).addListener(targetWriteListener);
                    targetChannel.read();
                } else {
                    log.debug("failed to connnect to target server: {}, error: {}",
                        targetHost, ThrowableUtil.stackTraceToString(future.cause()));
                    var res = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.BAD_GATEWAY);
                    proxyChannel.writeAndFlush(res);
                    proxyChannel.close();
                    targetChannel.close();
                }
            }
        });
        targetChannel = channelFuture.channel();
    }
}
