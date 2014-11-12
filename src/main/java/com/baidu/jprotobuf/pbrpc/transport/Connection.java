/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.ChannelFuture;

/**
 * RPC client connection
 * 
 * @author xiemalin
 * @since 1.0
 */
public class Connection {
    /**
     * 
     */
    private static final int MAX_REQUEST_SIZE = 102400;
    private ChannelFuture future;
    private AtomicBoolean isConnected = new AtomicBoolean();
    private BlockingQueue<RpcClientCallState> requestQueue;
    private RpcClient client;

    public Connection(RpcClient client) {
        this.isConnected.set(false);
        this.future = null;
        this.requestQueue = new ArrayBlockingQueue<RpcClientCallState>(MAX_REQUEST_SIZE);
        this.client = client;
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    public boolean isConnected() {
        return isConnected.get();
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected.set(isConnected);
    }

    public boolean produceRequest(RpcClientCallState state) {
        return requestQueue.add(state);
    }

    public RpcClientCallState consumeRequest() {
        return requestQueue.poll();
    }

    public RpcClient getRpcClient() {
        return this.client;
    }
}
