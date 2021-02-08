/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */

package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.RpcChannelErrorException;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcResponseMeta;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientCallState;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * RPC client service handler upon receive response data from server.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientServiceHandler extends SimpleChannelInboundHandler<RpcDataPackage> {

    /** log this class. */
    private static final Logger LOG = Logger.getLogger(RpcClientServiceHandler.class.getName());
    
    private boolean includeRemoteServerInfoOnError = false;

    /** RPC client. */
    private RpcClient rpcClient;

    /**
     * Instantiates a new rpc client service handler.
     *
     * @param rpcClient the rpc client
     */
    public RpcClientServiceHandler(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        if (rpcClient.getRpcClientOptions() != null) {
            includeRemoteServerInfoOnError = rpcClient.getRpcClientOptions().isIncludeRemoteServerInfoOnError();
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
     * java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDataPackage dataPackage) throws Exception {
        Long correlationId = dataPackage.getRpcMeta().getCorrelationId();
        RpcClientCallState state = rpcClient.removePendingRequest(correlationId);

        Integer errorCode = ErrorCodes.ST_SUCCESS;
        RpcResponseMeta response = dataPackage.getRpcMeta().getResponse();
        if (response != null) {
            errorCode = response.getErrorCode();
        }

        if (!ErrorCodes.isSuccess(errorCode)) {
            if (state != null) {
                String error = response.getErrorText();
                if (includeRemoteServerInfoOnError) {
                    SocketAddress remoteAddress = ctx.channel().remoteAddress();
                    error = "[error with remote info:" + remoteAddress + "]";
                }
                state.handleFailure(errorCode, error);
            } else {
                ctx.fireChannelReadComplete();
                throw new Exception(response.getErrorText());
            }
        } else {
            if (state != null) {
                state.setDataPackage(dataPackage);
                state.handleResponse(state.getDataPackage());
            }
        }
        ctx.fireChannelReadComplete();
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext,
     * java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // to remove broken channel from client handler
        try {
            handleChannelError(ctx, cause);
        } finally {
            LOG.log(Level.SEVERE, cause.getCause().getMessage(), cause.getCause());
        }

    }

    /**
     * Handle channel error.
     *
     * @param ctx the ctx
     * @param cause the cause
     */
    private void handleChannelError(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        Channel channel = ctx.channel();
        rpcClient.invalidBrokenChannel(channel, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        handleChannelError(ctx, new RpcChannelErrorException("Channel inactive error!"));
    }
}
