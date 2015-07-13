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

import java.lang.reflect.Method;

import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.jprotobuf.pbrpc.DummyServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.LogIDHolder;
import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware;
import com.baidu.jprotobuf.pbrpc.utils.ReflectionUtils;
import com.baidu.jprotobuf.pbrpc.utils.ServiceSignatureUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * default RPC handler for google protobuf generated code.
 * 
 * @author xiemalin
 * @since 1.0
 * @see RpcServiceRegistry
 */
@SuppressWarnings({"rawtypes"})
public abstract class AbstractAnnotationRpcHandler implements RpcHandler, RpcMetaAware {

    private String serviceName;
    private String methodName;
    private Method method;
    private Class inputClass;
    private Class outputClass;
    private Object service;
    private String description;

    private ServerAttachmentHandler attachmentHandler;
    protected String inputIDl;
    protected String outputIDL;
    
    /**
     * get the method
     * 
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * get the service
     * 
     * @return the service
     */
    public Object getService() {
        return service;
    }

    
    public RpcData doHandle(RpcData data) throws Exception {
        Long logId = data.getLogId();
        if (logId != null) {
            LogIDHolder.setCurrentLogid(logId);
        }
        try {
            return doRealHandle(data);
        } finally {
            LogIDHolder.clearLogId();
        }
    }
    
    protected abstract RpcData doRealHandle(RpcData data) throws Exception;

    /**
     * @param method
     * @param service
     * @param protobufPRCService
     */
    public AbstractAnnotationRpcHandler(Method method, Object service, ProtobufRPCService protobufPRCService) {
        super();
        this.method = method;
        this.service = service;
        this.description = protobufPRCService.description();
        serviceName = protobufPRCService.serviceName();
        methodName = protobufPRCService.methodName();
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }

        Class<?>[] types = method.getParameterTypes();
        if (types.length > 1) {
            throw new IllegalArgumentException("RPC method can not has more than one parameter. illegal method:"
                    + method.getName());
        } else if (types.length == 1) {
            inputClass = types[0];
        }

        Class<?> returnType = method.getReturnType();
        if (!ReflectionUtils.isVoid(returnType)) {
            outputClass = returnType;
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
        
        if (inputClass != null) {
            try {
                inputIDl = ProtobufIDLGenerator.getIDL(inputClass);
            } catch (Exception e) {
                inputIDl = null;
            }
        }
        
        if (outputClass != null) {
            try {
                outputIDL = ProtobufIDLGenerator.getIDL(outputClass);
            } catch (Exception e) {
                outputIDL = null;
            }
        }
    }
    
    public String getMethodSignature() {
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
        return methodSignature;
    }

    /**
     * get the inputClass
     * 
     * @return the inputClass
     */
    public Class getInputClass() {
        return inputClass;
    }

    /**
     * get the outputClass
     * 
     * @return the outputClass
     */
    public Class getOutputClass() {
        return outputClass;
    }

    /**
     * get the serviceName
     * 
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * set serviceName value to serviceName
     * 
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * get the methodName
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * get the attachmentHandler
     * @return the attachmentHandler
     */
    public ServerAttachmentHandler getAttachmentHandler() {
        return attachmentHandler;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware#getInputMetaProto()
     */
    public String getInputMetaProto() {
        return inputIDl;
    }
    
    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware#getOutputMetaProto()
     */
    public String getOutputMetaProto() {
        return outputIDL;
    }

    /**
     * get the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
