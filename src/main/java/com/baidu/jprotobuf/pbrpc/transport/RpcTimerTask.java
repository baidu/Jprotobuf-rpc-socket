/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

/**
 * Time task to process each request timeout event
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcTimerTask implements TimerTask {

    public static Logger LOG = Logger.getLogger(RpcTimerTask.class.getName());

    private long correlationId;
    private RpcClient rpcClient;

    public RpcTimerTask(long correlationId, RpcClient client) {
        this.correlationId = correlationId;
        this.rpcClient = client;
    }

    public void run(Timeout timeout) throws Exception {

        LOG.log(Level.FINE, "correlationId:" + correlationId + " timeout");
        RpcClientCallState state = rpcClient.removePendingRequest(correlationId);
        if (null != state) {
            state.handleTimeout();
        } else {
            LOG.log(Level.FINE, "correlationId:" + correlationId
                    + ": is timeout and no PendingClientCallState found for correlationId " + correlationId);

        }

    }

}
