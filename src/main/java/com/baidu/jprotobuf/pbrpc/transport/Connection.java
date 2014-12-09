/*
 * Copyright 2002-2014 the original author or authors.
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
     * max request default count
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
