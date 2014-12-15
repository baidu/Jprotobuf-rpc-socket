/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * Attachment byte array handler.
 *
 * @author xiemalin
 * @since 1.0
 * @see ProtobufPRC
 */
public interface ClientAttachmentHandler extends AttachmentHandler {

    /**
     * @param response
     * @param serviceName
     * @param methodName
     * @param params
     */
    void handleResponse(byte[] response, String serviceName, String methodName, Object... params);
}
