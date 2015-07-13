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
     * log id
     */
    private Long logId;

    /**
     * get the logId
     * 
     * @return the logId
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * set logId value to logId
     * 
     * @param logId
     *            the logId to set
     */
    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /**
     * get the data
     * 
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * set data value to data
     * 
     * @param data
     *            the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * get the attachment
     * 
     * @return the attachment
     */
    public byte[] getAttachment() {
        return attachment;
    }

    /**
     * set attachment value to attachment
     * 
     * @param attachment
     *            the attachment to set
     */
    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    /**
     * get the authenticationData
     * 
     * @return the authenticationData
     */
    public byte[] getAuthenticationData() {
        return authenticationData;
    }

    /**
     * set authenticationData value to authenticationData
     * 
     * @param authenticationData
     *            the authenticationData to set
     */
    public void setAuthenticationData(byte[] authenticationData) {
        this.authenticationData = authenticationData;
    }

    /**
     * get the extraParams
     * 
     * @return the extraParams
     */
    public byte[] getExtraParams() {
        return extraParams;
    }

    /**
     * set extraParams value to extraParams
     * 
     * @param extraParams
     *            the extraParams to set
     */
    public void setExtraParams(byte[] extraParams) {
        this.extraParams = extraParams;
    }

}
