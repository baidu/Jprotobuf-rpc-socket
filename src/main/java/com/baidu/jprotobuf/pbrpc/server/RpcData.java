/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

/**
 * RPC data object
 *
 * @author xiemalin
 * @since 1.1
 */
public class RpcData {

    private byte[] data;
    private byte[] attachment;
    private byte[] authenticationData;
    /**
     * get the data
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
    /**
     * set data value to data
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    /**
     * get the attachment
     * @return the attachment
     */
    public byte[] getAttachment() {
        return attachment;
    }
    /**
     * set attachment value to attachment
     * @param attachment the attachment to set
     */
    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
    /**
     * get the authenticationData
     * @return the authenticationData
     */
    public byte[] getAuthenticationData() {
        return authenticationData;
    }
    /**
     * set authenticationData value to authenticationData
     * @param authenticationData the authenticationData to set
     */
    public void setAuthenticationData(byte[] authenticationData) {
        this.authenticationData = authenticationData;
    }
    
    
}
