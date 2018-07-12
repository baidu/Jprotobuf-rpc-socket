/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */

package com.baidu.jprotobuf.pbrpc.transport;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.util.Timeout;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.transport.handler.ErrorCodes;
import com.google.protobuf.RpcCallback;

/**
 * RPC client call state.
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientCallState {

    /** The callback. */
    private RpcCallback<RpcDataPackage> callback;
    
    /** The start timestamp. */
    private long startTimestamp;
    
    /** The data package. */
    private RpcDataPackage dataPackage;
    
    /** The timeout. */
    private Timeout timeout;
    
    /** The channel. */
    private Channel channel;
    
    /**
     * Sets the channel.
     *
     * @param channel the new channel
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    /**
     * Gets the channel.
     *
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }
    
    /**
     * Checks if is current channel.
     *
     * @param channel the channel
     * @return true, if is current channel
     */
    public boolean isCurrentChannel(Channel channel) {
        if (this.channel == null) {
            return false;
        }
        
        return this.channel.equals(channel);
    }

    /**
     * Instantiates a new rpc client call state.
     *
     * @param callback the callback
     * @param dataPackage the data package
     * @param timeout the timeout
     */
    public RpcClientCallState(RpcCallback<RpcDataPackage> callback, RpcDataPackage dataPackage, Timeout timeout) {
        super();
        this.callback = callback;
        this.dataPackage = dataPackage;
        this.timeout = timeout;

        this.startTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets the callback.
     *
     * @return the callback
     */
    public RpcCallback<RpcDataPackage> getCallback() {
        return callback;
    }

    /**
     * Sets the callback.
     *
     * @param callback the new callback
     */
    public void setCallback(RpcCallback<RpcDataPackage> callback) {
        this.callback = callback;
    }

    /**
     * Gets the start timestamp.
     *
     * @return the start timestamp
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * Sets the start timestamp.
     *
     * @param startTimestamp the new start timestamp
     */
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Gets the data package.
     *
     * @return the data package
     */
    public RpcDataPackage getDataPackage() {
        return dataPackage;
    }

    /**
     * Sets the data package.
     *
     * @param dataPackage the new data package
     */
    public void setDataPackage(RpcDataPackage dataPackage) {
        this.dataPackage = dataPackage;
    }

    /**
     * Gets the timeout.
     *
     * @return the timeout
     */
    public Timeout getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout.
     *
     * @param timeout the new timeout
     */
    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    /**
     * Handle timeout.
     *
     * @param timeout the timeout
     * @param timeUnit the time unit
     */
    public void handleTimeout(long timeout, TimeUnit timeUnit, String message) {
        dataPackage.errorCode(ErrorCodes.ST_READ_TIMEOUT);
        String timeoutMessage = ErrorCodes.MSG_READ_TIMEOUT + timeout + "(" + timeUnit + ")";
        if (message != null) {
            timeoutMessage = timeoutMessage + " " + message;
        }
        dataPackage.errorText(timeoutMessage);

        callback(dataPackage);
    }

    /**
     * Callback.
     *
     * @param message the message
     */
    private void callback(RpcDataPackage message) {
        if (null != callback) {
            callback.run(message);
        }
    }
    
    /**
     * Handle failure.
     *
     * @param erroCode the erro code
     * @param message the message
     */
    public void handleFailure(int erroCode, String message) {
        dataPackage.errorCode(erroCode);
        dataPackage.errorText(message);
        this.timeout.cancel();
        callback(dataPackage);
    }

    /**
     * Handle failure.
     *
     * @param message the message
     */
    public void handleFailure(String message) {
        handleFailure(ErrorCodes.ST_ERROR, message);
    }

    /**
     * Handle response.
     *
     * @param response the response
     */
    public void handleResponse(RpcDataPackage response) {
        this.timeout.cancel();
        callback(response);
    }
}
