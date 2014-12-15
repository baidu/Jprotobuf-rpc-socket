/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * Server attachment handler.
 *
 * @author xiemalin
 * @since 1.1
 */
public interface ServerAttachmentHandler {

    
    /**
     * handle attachment from client and return new attachment response back to client 
     * 
     * @param response byte array receive from client
     * @param serviceName
     * @param methodName
     * @param params
     * @return attachment response back to client
     */
    byte[] handleAttachement(byte[] response, String serviceName, String methodName, Object... params);
}
