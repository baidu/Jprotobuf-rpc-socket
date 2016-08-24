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
import io.netty.channel.ChannelFutureListener;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A {@link ChannelFutureListener} implementation of RPC operation complete call back.
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcChannelFutureListener implements ChannelFutureListener {

    /** The log. */
    private static Logger LOG = Logger.getLogger(RpcChannelFutureListener.class.getName());

    /** The conn. */
    private Connection conn;

    /**
     * Instantiates a new rpc channel future listener.
     *
     * @param conn the conn
     */
    public RpcChannelFutureListener(Connection conn) {
        this.conn = conn;
    }

    /* (non-Javadoc)
     * @see io.netty.util.concurrent.GenericFutureListener#operationComplete(io.netty.util.concurrent.Future)
     */
    public void operationComplete(ChannelFuture future) throws Exception {

        if (!future.isSuccess()) {
            LOG.log(Level.WARNING, "build channel:" + future.channel() + " failed");
            conn.setIsConnected(false);
            return;
        }

        RpcClientCallState requestState = null;
        while (null != (requestState = conn.consumeRequest())) {
            LOG.log(Level.FINEST, "[correlationId:" + requestState.getDataPackage().getRpcMeta().getCorrelationId()
                    + "] send over from queue");
            conn.getFuture().channel().writeAndFlush(requestState.getDataPackage());
        }
    }

}
