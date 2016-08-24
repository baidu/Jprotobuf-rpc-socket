/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.jprotobuf.pbrpc.client;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.jprotobuf.pbrpc.ProtobufRPC;

/**
 * RPC method description info for JProtobuf annotation.
 *
 * @author xiemalin
 * @since 1.0
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class PojoRpcMethodInfo extends RpcMethodInfo {
    
    /** The input codec. */
    private Codec inputCodec;
    
    /** The output codec. */
    private Codec outputCodec;

    /**
     * Instantiates a new pojo rpc method info.
     *
     * @param method the method
     * @param protobufPRC the protobuf prc
     */
    public PojoRpcMethodInfo(Method method, ProtobufRPC protobufPRC) {
        super(method, protobufPRC);
        
        Class<? extends Object> inputClass = getInputClass();
        if (inputClass != null) {
            inputCodec = ProtobufProxy.create(inputClass);
        }
        Class<? extends Object> outputClass = getOutputClass();
        if (outputClass != null) {
        	// future type return
        	if (outputClass.isAssignableFrom(Future.class)) {
        		
        		Type genericReturnType = method.getGenericReturnType();
        		if (genericReturnType instanceof ParameterizedType) {
        			ParameterizedType pt = (ParameterizedType) genericReturnType;
        			Type[] types = pt.getActualTypeArguments();
        			if (types != null && types.length == 1) {
        				outputClass = (Class) types[0];
        			} else {
        				outputClass = null;
        			}
        		} else {
        			outputClass = null;
        		}
        	}
        	
        	if (outputClass != null) {
        		outputCodec = ProtobufProxy.create(outputClass);
        	}
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
