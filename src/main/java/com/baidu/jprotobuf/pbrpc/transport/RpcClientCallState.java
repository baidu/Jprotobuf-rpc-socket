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

import org.jboss.netty.util.Timeout;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.transport.handler.ErrorCodes;
import com.google.protobuf.RpcCallback;

/**
 * RPC client call state
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientCallState {

    private RpcCallback<RpcDataPackage> callback;
    private long startTimestamp;
    private RpcDataPackage dataPackage;
    private Timeout timeout;

    /**
     * @param callback
     * @param startTimestamp
     * @param dataPackage
     * @param timeout
     */
    public RpcClientCallState(RpcCallback<RpcDataPackage> callback, RpcDataPackage dataPackage, Timeout timeout) {
        super();
        this.callback = callback;
        this.dataPackage = dataPackage;
        this.timeout = timeout;

        this.startTimestamp = System.currentTimeMillis();
    }

    /**
     * get the callback
     * 
     * @return the callback
     */
    public RpcCallback<RpcDataPackage> getCallback() {
        return callback;
    }

    /**
     * set callback value to callback
     * 
     * @param callback
     *            the callback to set
     */
    public void setCallback(RpcCallback<RpcDataPackage> callback) {
        this.callback = callback;
    }

    /**
     * get the startTimestamp
     * 
     * @return the startTimestamp
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * set startTimestamp value to startTimestamp
     * 
     * @param startTimestamp
     *            the startTimestamp to set
     */
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * get the dataPackage
     * 
     * @return the dataPackage
     */
    public RpcDataPackage getDataPackage() {
        return dataPackage;
    }

    /**
     * set dataPackage value to dataPackage
     * 
     * @param dataPackage
     *            the dataPackage to set
     */
    public void setDataPackage(RpcDataPackage dataPackage) {
        this.dataPackage = dataPackage;
    }

    /**
     * get the timeout
     * 
     * @return the timeout
     */
    public Timeout getTimeout() {
        return timeout;
    }

    /**
     * set timeout value to timeout
     * 
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public void handleTimeout() {
        dataPackage.errorCode(ErrorCodes.ST_READ_TIMEOUT);
        dataPackage.errorText(ErrorCodes.MSG_READ_TIMEOUT);

        callback(dataPackage);
    }

    private void callback(RpcDataPackage message) {
        if (null != callback) {
            callback.run(message);
        }
    }
    
    public void handleFailure(int erroCode, String message) {
        dataPackage.errorCode(erroCode);
        dataPackage.errorText(message);
        this.timeout.cancel();
        callback(dataPackage);
    }

    public void handleFailure(String message) {
        handleFailure(ErrorCodes.ST_ERROR, message);
    }

    public void handleResponse(RpcDataPackage response) {
        this.timeout.cancel();
        callback(response);
    }
}
