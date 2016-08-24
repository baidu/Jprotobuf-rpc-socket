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

import io.netty.util.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.google.protobuf.RpcCallback;

/**
 * RPC request and response channel processor.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcChannel {

    /** The log. */
    private static Logger LOG = Logger.getLogger(RpcChannel.class.getName());

    /** RPC client. */
    private RpcClient rpcClient;
    
    /** The channel pool. */
    private ChannelPool channelPool;

    /**
     * try to do connect.
     */
    public void testChannlConnect() {
        Connection channel = channelPool.getChannel();
        channelPool.returnChannel(channel);
    }

    /**
     * Instantiates a new rpc channel.
     *
     * @param rpcClient the rpc client
     * @param host the host
     * @param port the port
     */
    public RpcChannel(RpcClient rpcClient, String host, int port) {
        this.rpcClient = rpcClient;
        channelPool = new ChannelPool(rpcClient, host, port);
        rpcClient.setChannelPool(channelPool);
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        Connection channel = channelPool.getChannel();
        return channel;
    }

    /**
     * Release connection.
     *
     * @param connection the connection
     */
    public void releaseConnection(Connection connection) {
        channelPool.returnChannel(connection);
    }

    /**
     * Do transport.
     *
     * @param connection the connection
     * @param rpcDataPackage the rpc data package
     * @param callback the callback
     * @param onceTalkTimeout the once talk timeout
     */
    public void doTransport(Connection connection, RpcDataPackage rpcDataPackage, RpcCallback<RpcDataPackage> callback,
            long onceTalkTimeout) {
        if (rpcDataPackage == null) {
            throw new IllegalArgumentException("param 'rpcDataPackage' is null.");
        }

        long callMethodStart = System.currentTimeMillis();

        // register timer
        Timeout timeout =
                rpcClient.getTimer()
                        .newTimeout(
                                new RpcTimerTask(rpcDataPackage.getRpcMeta().getCorrelationId(), this.rpcClient,
                                        onceTalkTimeout, TimeUnit.MILLISECONDS),
                                onceTalkTimeout, TimeUnit.MILLISECONDS);

        RpcClientCallState state = new RpcClientCallState(callback, rpcDataPackage, timeout);

        Long correlationId = state.getDataPackage().getRpcMeta().getCorrelationId();
        rpcClient.registerPendingRequest(correlationId, state);

        if (!connection.getFuture().isSuccess()) {
            try {
                connection.produceRequest(state);
            } catch (IllegalStateException e) {
                RpcClientCallState callState = rpcClient.removePendingRequest(correlationId);
                if (callState != null) {
                    callState.handleFailure(e.getMessage());
                    LOG.log(Level.FINE, "id:" + correlationId + " is put in the queue");
                }
            }
        } else {
            connection.getFuture().channel().writeAndFlush(state.getDataPackage());
        }

        long callMethodEnd = System.currentTimeMillis();
        LOG.log(Level.FINE, "profiling callMethod cost " + (callMethodEnd - callMethodStart) + "ms");

    }

    /**
     * Close.
     */
    public void close() {
        if (channelPool != null) {
            channelPool.stop();
        }

    }

}
