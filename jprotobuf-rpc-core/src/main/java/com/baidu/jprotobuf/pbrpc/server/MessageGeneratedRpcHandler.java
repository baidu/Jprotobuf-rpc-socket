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

package com.baidu.jprotobuf.pbrpc.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.intercept.MethodInvocationInfo;
import com.google.protobuf.GeneratedMessage;

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
            if (GeneratedMessage.class.isAssignableFrom(getInputClass())) {
                try {
                    parseFromMethod = getInputClass().getMethod(PROTOBUF_PARSE_METHOD, InputStream.class);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#doRealHandle(com.baidu.jprotobuf. pbrpc .server.RpcData)
     */
    protected RpcData doRealHandle(RpcData data) throws Exception {

        Object input = null;
        Object[] param;
        Object ret = null;
        if (data.getData() != null && parseFromMethod != null) {
            input = parseFromMethod.invoke(getInputClass(), new ByteArrayInputStream(data.getData()));
            param = new Object[] { input };
        } else {
            param = new Object[0];
        }

        RpcData retData = new RpcData();
        
        // process authentication data handler
        if (getAuthenticationHandler() != null) {
            getAuthenticationHandler().handle(data.getAuthenticationData(), getServiceName(),
                    getMethodName(), param);
        }
        
        // process attachment
        if (getAttachmentHandler() != null) {
            byte[] responseAttachment = getAttachmentHandler().handleAttachement(data.getAttachment(), getServiceName(),
                    getMethodName(), param);
            retData.setAttachment(responseAttachment);
        }

        long time = System.currentTimeMillis();

        try {
            // check intercepter
            if (getInterceptor() != null) {
                MethodInvocationInfo methodInvocationInfo =
                        new MethodInvocationInfo(getService(), param, getMethod(), data.getExtraParams());
                getInterceptor().beforeInvoke(methodInvocationInfo);

                ret = getInterceptor().process(methodInvocationInfo);
                if (ret != null) {
                    PERFORMANCE_LOGGER.fine("RPC client invoke method(by intercepter) '" + getMethod().getName()
                            + "' time took:" + (System.currentTimeMillis() - time) + " ms");

                    if (ret instanceof GeneratedMessage) {
                        byte[] response = ((GeneratedMessage) ret).toByteArray();
                        retData.setData(response);
                    }

                    return retData;
                }
            }

            ret = getMethod().invoke(getService(), param);
            long took = (System.currentTimeMillis() - time);
            PERFORMANCE_LOGGER
                    .fine("RPC server invoke method(local) '" + getMethod().getName() + "' time took:" + took + " ms");

            if (ret == null) {
                return retData;
            }

            if (ret != null && ret instanceof GeneratedMessage) {
                byte[] response = ((GeneratedMessage) ret).toByteArray();
                retData.setData(response);
            }

            return retData;
        } finally {
            if (getInterceptor() != null) {
                getInterceptor().afterProcess();
            }
        }
    }

}
