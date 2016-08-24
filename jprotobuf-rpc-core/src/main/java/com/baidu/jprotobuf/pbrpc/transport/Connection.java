/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.jprotobuf.pbrpc.transport;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RPC client connection .
 *
 * @author xiemalin
 * @since 1.0
 */
public class Connection {
    
    /** max request default count. */
    private static final int MAX_REQUEST_SIZE = 102400;
    
    /** The future. */
    private ChannelFuture future;
    
    /** The is connected. */
    private AtomicBoolean isConnected = new AtomicBoolean();
    
    /** The request queue. */
    private BlockingQueue<RpcClientCallState> requestQueue;
    
    /** The client. */
    private RpcClient client;

    /**
     * Instantiates a new connection.
     *
     * @param client the client
     */
    public Connection(RpcClient client) {
        this.isConnected.set(false);
        this.future = null;
        this.requestQueue = new ArrayBlockingQueue<RpcClientCallState>(MAX_REQUEST_SIZE);
        this.client = client;
    }

    /**
     * Gets the future.
     *
     * @return the future
     */
    public ChannelFuture getFuture() {
        return future;
    }

    /**
     * Sets the future.
     *
     * @param future the new future
     */
    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return isConnected.get();
    }

    /**
     * Sets the checks if is connected.
     *
     * @param isConnected the new checks if is connected
     */
    public void setIsConnected(boolean isConnected) {
        this.isConnected.set(isConnected);
    }

    /**
     * Produce request.
     *
     * @param state the state
     * @return true, if successful
     */
    public boolean produceRequest(RpcClientCallState state) {
        return requestQueue.add(state);
    }

    /**
     * Consume request.
     *
     * @return the rpc client call state
     */
    public RpcClientCallState consumeRequest() {
        return requestQueue.poll();
    }
    
    /**
     * Clear requests.
     */
    public void clearRequests() {
        requestQueue.clear();
    }

    /**
     * Gets the rpc client.
     *
     * @return the rpc client
     */
    public RpcClient getRpcClient() {
        return this.client;
    }
}
