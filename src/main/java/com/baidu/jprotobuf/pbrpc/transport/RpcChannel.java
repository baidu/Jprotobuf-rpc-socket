/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.Timeout;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.google.protobuf.RpcCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RPC request and response channel processor.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcChannel {

    private static Logger LOG = Logger.getLogger(RpcChannel.class.getName());

    /**
     * RPC client
     */
    private RpcClient rpcClient;
    private ChannelPool channelPool;

    /**
     * @param rpcClient
     * @param host
     * @param port
     */
    public RpcChannel(RpcClient rpcClient, String host, int port) {
        this.rpcClient = rpcClient;
        channelPool = new ChannelPool(rpcClient, host, port);
        rpcClient.setChannelPool(channelPool);
    }

    public void doTransport(RpcDataPackage rpcDataPackage, RpcCallback<RpcDataPackage> callback, long onceTalkTimeout) {
        if (rpcDataPackage == null) {
            throw new IllegalArgumentException("param 'rpcDataPackage' is null.");
        }

        long callMethodStart = System.currentTimeMillis();

        // set correlationId
        rpcDataPackage.getRpcMeta().setCorrelationId(rpcClient.getNextCorrelationId());

        // register timer
        Timeout timeout = rpcClient.getTimer().newTimeout(
                new RpcTimerTask(rpcDataPackage.getRpcMeta().getCorrelationId(), this.rpcClient), onceTalkTimeout,
                TimeUnit.MILLISECONDS);

        RpcClientCallState state = new RpcClientCallState(callback, rpcDataPackage, timeout);

        Long correlationId = state.getDataPackage().getRpcMeta().getCorrelationId();

        rpcClient.registerPendingRequest(correlationId, state);

        Connection channel = channelPool.getChannel();

        try {
            if (!channel.getFuture().isSuccess()) {
                try {
                    channel.produceRequest(state);
                } catch (IllegalStateException e) {
                    RpcClientCallState callState = rpcClient.removePendingRequest(correlationId);
                    callState.handleFailure(e.getMessage());
                }
                LOG.log(Level.FINE, "id:" + correlationId + "is put in the queue");

            } else {
                channel.getFuture().getChannel().write(state.getDataPackage());
            }

            long callMethodEnd = System.currentTimeMillis();
            LOG.log(Level.FINE, "profiling callMethod cost " + (callMethodEnd - callMethodStart) + "ms");
        } finally {
            channelPool.returnChannel(channel);
        }

    }

}
