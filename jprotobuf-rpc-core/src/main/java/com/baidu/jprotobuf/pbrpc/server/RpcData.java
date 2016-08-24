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

package com.baidu.jprotobuf.pbrpc.server;

/**
 * RPC data object.
 *
 * @author xiemalin
 * @since 1.1
 */
public class RpcData {

    /** user data. */
    private byte[] data;

    /** user attachment data. */
    private byte[] attachment;

    /** user authentiction data. */
    private byte[] authenticationData;

    /** extra params. */
    private byte[] extraParams;

    /** log id. */
    private Long logId;

    /**
     * Gets the log id.
     *
     * @return the log id
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * Sets the log id.
     *
     * @param logId the new log id
     */
    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /**
     * Gets the user data.
     *
     * @return the user data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the user data.
     *
     * @param data the new user data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Gets the user attachment data.
     *
     * @return the user attachment data
     */
    public byte[] getAttachment() {
        return attachment;
    }

    /**
     * Sets the user attachment data.
     *
     * @param attachment the new user attachment data
     */
    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    /**
     * Gets the user authentiction data.
     *
     * @return the user authentiction data
     */
    public byte[] getAuthenticationData() {
        return authenticationData;
    }

    /**
     * Sets the user authentiction data.
     *
     * @param authenticationData the new user authentiction data
     */
    public void setAuthenticationData(byte[] authenticationData) {
        this.authenticationData = authenticationData;
    }

    /**
     * Gets the extra params.
     *
     * @return the extra params
     */
    public byte[] getExtraParams() {
        return extraParams;
    }

    /**
     * Sets the extra params.
     *
     * @param extraParams the new extra params
     */
    public void setExtraParams(byte[] extraParams) {
        this.extraParams = extraParams;
    }

}
