/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A {@link ChannelFutureListener} implementation of RPC operation complete call back
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcChannelFutureListener implements ChannelFutureListener {

    private static Logger LOG = Logger.getLogger(RpcChannelFutureListener.class.getName());

    private Connection conn;

    public RpcChannelFutureListener(Connection conn) {
        this.conn = conn;
    }

    public void operationComplete(ChannelFuture future) throws Exception {

        if (!future.isSuccess()) {
            LOG.log(Level.WARNING, "build channel:" + future.getChannel().getId() + " failed");
            conn.setIsConnected(false);
            return;
        }

        RpcClientCallState requestState = null;
        while (null != (requestState = conn.consumeRequest())) {
            LOG.log(Level.FINEST, "[correlationId:" + requestState.getDataPackage().getRpcMeta().getCorrelationId()
                    + "] send over from queue");
            conn.getFuture().getChannel().write(requestState.getDataPackage());
        }
    }

}
