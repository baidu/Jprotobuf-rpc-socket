/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * Dummy {@link ClientAttachmentHandler} implements
 * 
 * @author xiemalin
 * @since 1.0
 * @see ProtobufPRC
 */
public class DummyClientAttachmentHandler extends DummyAttachmentHandler implements ClientAttachmentHandler {


    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler#handleResponse(byte[], java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void handleResponse(byte[] response, String serviceName, String methodName, Object... params) {
        
    }

}
