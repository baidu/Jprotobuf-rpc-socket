/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * attachment handle service.
 *
 * @author xiemalin
 * @since 1.0
 * @see ProtobufPRCService
 */
public interface AttachmentHandler {

    /**
     * To add attachment for each RPC method invoke
     * 
     * @param serviceName the service name
     * @param methodName the method name
     * @param params method params
     * @return attachment byte array to send
     */
    byte[] handleRequest(String serviceName, String methodName, Object... params);
}
