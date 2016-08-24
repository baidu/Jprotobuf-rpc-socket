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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.google.protobuf.GeneratedMessage;

/**
 * RPC method description info for Google generated java code.
 *
 * @author xiemalin
 * @since 1.2
 */
public class GeneratedMessageRpcMethodInfo extends RpcMethodInfo {

    /** The Constant PROTOBUF_PARSE_METHOD. */
    private static final String PROTOBUF_PARSE_METHOD = "parseFrom";

    /** The parse from method. */
    private Method parseFromMethod;

    /**
     * Instantiates a new generated message rpc method info.
     *
     * @param method the method
     * @param protobufPRC the protobuf prc
     */
    public GeneratedMessageRpcMethodInfo(Method method, ProtobufRPC protobufPRC) {
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
