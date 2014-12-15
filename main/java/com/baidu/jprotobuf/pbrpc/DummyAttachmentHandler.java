/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * Dummy {@link AttachmentHandler} implements.
 *
 * @author xiemalin
 * @since 1.0
 */
public class DummyAttachmentHandler implements AttachmentHandler {

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.AttachmentHandler#handleRequest(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public byte[] handleRequest(String serviceName, String methodName, Object... params) {
        return null;
    }

}
