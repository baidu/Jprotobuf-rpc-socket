/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.baidu.jprotobuf.pbrpc.ProtobufPRC;
import com.google.protobuf.GeneratedMessage;

/**
 * RPC method description info for Google generated java code
 * 
 * @author xiemalin
 * @since 1.2
 */
public class GeneratedMessageRpcMethodInfo extends RpcMethodInfo {

    private static final String PROTOBUF_PARSE_METHOD = "parseFrom";

    private Method parseFromMethod;

    /**
     * @param method
     * @param protobufPRC
     */
    public GeneratedMessageRpcMethodInfo(Method method, ProtobufPRC protobufPRC) {
        super(method, protobufPRC);

        Class<?> outputClass = getOutputClass();
        if (outputClass != null) {
            if (GeneratedMessage.class.isAssignableFrom(outputClass)) {
                try {
                    parseFromMethod = outputClass.getMethod(PROTOBUF_PARSE_METHOD, InputStream.class);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo#inputEncode(java.lang.
     * Object)
     */
    @Override
    public byte[] inputEncode(Object input) throws IOException {
        if (input instanceof GeneratedMessage) {
            return ((GeneratedMessage) input).toByteArray();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo#outputDecode(byte[])
     */
    @Override
    public Object outputDecode(byte[] output) throws IOException {
        Class<?> outputClass = getOutputClass();
        if (parseFromMethod != null && output != null) {
            try {
                return parseFromMethod.invoke(outputClass, new ByteArrayInputStream(output));
            } catch (Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        return null;
    }

}
