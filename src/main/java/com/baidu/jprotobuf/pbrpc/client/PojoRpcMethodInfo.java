/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.client;

import java.io.IOException;
import java.lang.reflect.Method;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.jprotobuf.pbrpc.ProtobufPRC;

/**
 * RPC method description info for JProtobuf annotation.
 *
 * @author xiemalin
 * @since 1.0
 */
public class PojoRpcMethodInfo extends RpcMethodInfo {
    
    private Codec inputCodec;
    
    private Codec outputCodec;

    /**
     * @param method
     * @param protobufPRC
     */
    public PojoRpcMethodInfo(Method method, ProtobufPRC protobufPRC) {
        super(method, protobufPRC);
        
        Class<? extends Object> inputClass = getInputClass();
        if (inputClass != null) {
            inputCodec = ProtobufProxy.create(inputClass);
        }
        Class<? extends Object> outputClass = getOutputClass();
        if (outputClass != null) {
            outputCodec = ProtobufProxy.create(outputClass);
        }
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo#inputDecode(java.lang.Object)
     */
    @Override
    public byte[] inputEncode(Object input) throws IOException {
        if (inputCodec != null) {
            return inputCodec.encode(input);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo#outputEncode(byte[])
     */
    @Override
    public Object outputDecode(byte[] output) throws IOException {
        if (outputCodec != null) {
            return outputCodec.decode(output);
        }
        return null;
    }
    
}
