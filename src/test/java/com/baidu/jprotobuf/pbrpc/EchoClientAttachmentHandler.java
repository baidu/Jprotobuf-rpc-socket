/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;


import org.junit.Assert;

/**
 * Echo test {@link ClientAttachmentHandler}
 * 
 * @author xiemalin
 * @since 1.0
 */
public class EchoClientAttachmentHandler implements ClientAttachmentHandler {
    
    private byte[] attachment = EchoClientAttachmentHandler.class.getName().getBytes();

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.AttachmentHandler#handleRequest(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public byte[] handleRequest(String serviceName, String methodName, Object... params) {
        if ("echoWithAttachement".equals(methodName)) {
            return attachment;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler#handleResponse(byte[], java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void handleResponse(byte[] response, String serviceName, String methodName, Object... params) {
        if ("echoWithAttachement".equals(methodName)) {
            Assert.assertEquals(EchoServerAttachmentHandler.class.getName(), new String(response));
        }

    }

}
