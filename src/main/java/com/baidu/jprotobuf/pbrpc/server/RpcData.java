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

    /**
     * user data
     */
    private byte[] data;
    
    /**
     * user attachment data
     */
    private byte[] attachment;
    
    /**
     * user authentiction data
     */
    private byte[] authenticationData;
    
    /**
     * extra params
     */
    private byte[] extraParams;
    
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
    /**
     * get the extraParams
     * @return the extraParams
     */
    public byte[] getExtraParams() {
        return extraParams;
    }
    /**
     * set extraParams value to extraParams
     * @param extraParams the extraParams to set
     */
    public void setExtraParams(byte[] extraParams) {
        this.extraParams = extraParams;
    }
    
    
}
