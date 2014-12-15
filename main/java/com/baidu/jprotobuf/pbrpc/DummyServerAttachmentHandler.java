/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * 
 * Dummy {@link ServerAttachmentHandler}
 * 
 * @author xiemalin
 * @since 1.1
 */
public class DummyServerAttachmentHandler implements ServerAttachmentHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler#handleAttachement(byte
     * [], java.lang.String, java.lang.String, java.lang.Object[])
     */
    public byte[] handleAttachement(byte[] response, String serviceName, String methodName, Object... params) {
        return null;
    }

}
