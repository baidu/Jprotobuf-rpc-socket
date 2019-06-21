/*
 * Copyright 2002-2014 the original author or authors.
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

package com.baidu.jprotobuf.pbrpc.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo;
import com.google.protobuf.AbstractMessage;

/**
 * RPC handler for Google protoc generated java code.
 * 
 * @author xiemalin
 * @since 1.2
 */
@SuppressWarnings({ "unchecked" })
public class MessageGeneratedRpcHandler extends AbstractAnnotationRpcHandler {

    /** Logger for this class. */
    private static final Logger PERFORMANCE_LOGGER = Logger.getLogger("performance-log");

    /** The Constant PROTOBUF_PARSE_METHOD. */
    private static final String PROTOBUF_PARSE_METHOD = "parseFrom";

    /** The parse from method. */
    private Method parseFromMethod;

    /**
     * Instantiates a new message generated rpc handler.
     *
     * @param method the method
     * @param service the service
     * @param protobufPRCService the protobuf prc service
     */
    public MessageGeneratedRpcHandler(Method method, Object service, ProtobufRPCService protobufPRCService) {
        super(method, service, protobufPRCService);

        if (getInputClass() != null) {
            if (RpcMethodInfo.isMessageType(getInputClass())) {
                try {
                    parseFromMethod = getInputClass().getMethod(PROTOBUF_PARSE_METHOD, InputStream.class);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * Decode output param.
     *
     * @param ret the ret
     * @return the byte[]
     * @throws Exception the exception
     */
    @Override
    protected byte[] decodeOutputParam(Object ret) throws Exception {
        if (ret != null && ret instanceof AbstractMessage) {
            byte[] response = ((AbstractMessage) ret).toByteArray();
            return response;
        }
        return null;
    }

    @Override
    protected Object encodeInputParam(byte[] data) throws Exception {
        if (data != null && parseFromMethod != null) {
            Object input = parseFromMethod.invoke(getInputClass(), new ByteArrayInputStream(data));
            return input;
        }

        return null;
    }

}
