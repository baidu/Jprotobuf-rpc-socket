/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

import java.lang.reflect.Method;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.jprotobuf.pbrpc.DummyServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.ProtobufPRCService;
import com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler;

/**
 * RPC handler for Jprotobuf annotation.
 *
 * @author xiemalin
 * @since 1.0
 */
public class AnnotationRpcHandler extends AbstractRpcHandler {

    private Codec inputCodec;
    private Codec outputCodec;
    private ServerAttachmentHandler attachmentHandler;
    /**
     * @param method
     * @param service
     */
    public AnnotationRpcHandler(Method method, Object service, ProtobufPRCService protobufPRCService) {
        super(method, service, protobufPRCService);
        if (getInputClass() != null) {
            inputCodec = ProtobufProxy.create(getInputClass());
        }
        if (getOutputClass() != null) {
            outputCodec = ProtobufProxy.create(getOutputClass());
        }
        
        // process attachment handler
        Class<? extends ServerAttachmentHandler> attachmentHandlerClass = protobufPRCService.attachmentHandler();
        if (attachmentHandlerClass != DummyServerAttachmentHandler.class) {
            try {
                attachmentHandler = attachmentHandlerClass.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'logIDGenerator' of class '"
                        + attachmentHandlerClass.getName() + "'");
            }
        }
        
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#doHandle(byte[])
     */
    public RpcData doHandle(RpcData data) throws Exception {
        Object input = null;
        Object[] param;
        Object ret;
        if (data.getData() != null && inputCodec != null) {
            input = inputCodec.decode(data.getData());
            param = new Object[] {input};
        } else {
            param = new Object[0];
        }
        
        RpcData retData = new RpcData();
        // process attachment
        if (attachmentHandler != null) {
            byte[] responseAttachment = attachmentHandler.handleAttachement(data.getAttachment(), getServiceName(), getMethodName(), param);
            retData.setAttachment(responseAttachment);
        }
        
        ret = getMethod().invoke(getService(), param);
        
        if (ret == null) {
            return retData;
        }
        
        if (outputCodec != null) {
            byte[] response = outputCodec.encode(ret);
            retData.setData(response);
        }
        
        return retData;
    }


    
    

}
