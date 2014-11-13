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
 * Echo test {@link ServerAttachmentHandler}
 * 
 * @author xiemalin
 * @since 1.1
 */
public class EchoServerAttachmentHandler implements ServerAttachmentHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler#handleAttachement(byte
     * [], java.lang.String, java.lang.String, java.lang.Object[])
     */
    public byte[] handleAttachement(byte[] response, String serviceName, String methodName, Object... params) {
        Assert.assertEquals(EchoClientAttachmentHandler.class.getName(), new String(response));
        return EchoServerAttachmentHandler.class.getName().getBytes();
    }

}
