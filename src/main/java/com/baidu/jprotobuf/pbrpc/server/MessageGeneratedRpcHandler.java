/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.baidu.jprotobuf.pbrpc.ProtobufPRCService;
import com.google.protobuf.GeneratedMessage;

/**
 * RPC handler for Google protoc generated java code.
 *
 * @author xiemalin
 * @since 1.2
 */
public class MessageGeneratedRpcHandler extends AbstractRpcHandler {
    
    private static final String PROTOBUF_PARSE_METHOD = "parseFrom";

    private Method parseFromMethod;
    
    /**
     * @param method
     * @param service
     * @param protobufPRCService
     */
    public MessageGeneratedRpcHandler(Method method, Object service, ProtobufPRCService protobufPRCService) {
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

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#doHandle(com.baidu.jprotobuf.pbrpc.server.RpcData)
     */
    public RpcData doHandle(RpcData data) throws Exception {
        
        Object input = null;
        Object[] param;
        Object ret;
        if (data.getData() != null && parseFromMethod != null) {
            input = parseFromMethod.invoke(getInputClass(), new ByteArrayInputStream(data.getData()));;
            param = new Object[] {input};
        } else {
            param = new Object[0];
        }
        
        RpcData retData = new RpcData();
        // process attachment
        if (getAttachmentHandler() != null) {
            byte[] responseAttachment = getAttachmentHandler().handleAttachement(data.getAttachment(), getServiceName(), getMethodName(), param);
            retData.setAttachment(responseAttachment);
        }
        
        ret = getMethod().invoke(getService(), param);
        
        if (ret == null) {
            return retData;
        }
        
        if (ret != null && ret instanceof GeneratedMessage) {
            byte[] response = ((GeneratedMessage) input).toByteArray();
            retData.setData(response);
        }
        
        return retData;
    }

}
