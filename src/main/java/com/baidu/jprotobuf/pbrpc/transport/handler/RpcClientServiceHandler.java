/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientCallState;

/**
 * RPC client service handler upon receive response data from server.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientServiceHandler extends SimpleChannelUpstreamHandler {

    /**
     * RPC client
     */
    private RpcClient rpcClient;

    /**
     * @param rpcClient
     */
    public RpcClientServiceHandler(RpcClient rpcClient) {
        super();
        this.rpcClient = rpcClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(
     * org.jboss.netty.channel.ChannelHandlerContext,
     * org.jboss.netty.channel.MessageEvent)
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e.getMessage() instanceof RpcDataPackage)) {
            return;
        }

        RpcDataPackage dataPackage = (RpcDataPackage) e.getMessage();
        Long correlationId = dataPackage.getRpcMeta().getCorrelationId();
        RpcClientCallState state = rpcClient.removePendingRequest(correlationId);
        
        if (state != null) {
            
            Integer errorCode = dataPackage.getRpcMeta().getResponse().getErrorCode();
            if (! ErrorCodes.isSuccess(errorCode)) {
                state.getDataPackage().errorCode(errorCode).errorText(
                        dataPackage.getRpcMeta().getResponse().getErrorText());
            }
            state.setDataPackage(dataPackage);
            state.handleResponse(state.getDataPackage());
        }
        

        ctx.sendUpstream(e);
    }

}
