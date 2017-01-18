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

import java.lang.reflect.Method;

import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.jprotobuf.pbrpc.DummyServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.DummyServerAuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.LogIDHolder;
import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.ServerAuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware;
import com.baidu.jprotobuf.pbrpc.utils.ReflectionUtils;
import com.baidu.jprotobuf.pbrpc.utils.ServiceSignatureUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * default RPC handler for google protobuf generated code.
 *
 * @author xiemalin
 * @see RpcServiceRegistry
 * @since 1.0
 */
@SuppressWarnings({"rawtypes"})
public abstract class AbstractAnnotationRpcHandler implements RpcHandler, RpcMetaAware {

    /** The service name. */
    private String serviceName;
    
    /** The method name. */
    private String methodName;
    
    /** The method. */
    private Method method;
    
    /** The input class. */
    private Class inputClass;
    
    /** The output class. */
    private Class outputClass;
    
    /** The service. */
    private Object service;
    
    /** The description. */
    private String description;

    /** The attachment handler. */
    private ServerAttachmentHandler attachmentHandler;
    
    /** The input i dl. */
    protected String inputIDl;
    
    /** The output idl. */
    protected String outputIDL;
    
	/** The interceptor. */
	private InvokerInterceptor interceptor;

    private ServerAuthenticationDataHandler authenticationHandler;

	/**
	 * Sets the interceptor.
	 *
	 * @param interceptor the new interceptor
	 */
	public void setInterceptor(InvokerInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	
	/**
	 * Gets the interceptor.
	 *
	 * @return the interceptor
	 */
	protected InvokerInterceptor getInterceptor() {
		return interceptor;
	}
    
    /**
     * Gets the method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getService()
     */
    public Object getService() {
        return service;
    }

    
    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#doHandle(com.baidu.jprotobuf.pbrpc.server.RpcData)
     */
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
    
    /**
     * Do real handle.
     *
     * @param data the data
     * @return the rpc data
     * @throws Exception the exception
     */
    protected abstract RpcData doRealHandle(RpcData data) throws Exception;

    /**
     * Instantiates a new abstract annotation rpc handler.
     *
     * @param method the method
     * @param service the service
     * @param protobufPRCService the protobuf prc service
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
                throw new IllegalAccessError("Can not initialize 'ServerAttachmentHandler' of class '"
                        + attachmentHandlerClass.getName() + "'");
            }
        }
        
        // process authentication data handler
        Class<? extends ServerAuthenticationDataHandler> authClass = protobufPRCService.authenticationDataHandler();
        if (authClass != DummyServerAuthenticationDataHandler.class) {
            try {
                authenticationHandler = authClass.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'ServerAuthenticationDataHandler' of class '"
                        + authClass.getName() + "'");
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
    
    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getMethodSignature()
     */
    public String getMethodSignature() {
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
        return methodSignature;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getInputClass()
     */
    public Class getInputClass() {
        return inputClass;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getOutputClass()
     */
    public Class getOutputClass() {
        return outputClass;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getServiceName()
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the service name.
     *
     * @param serviceName the new service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getMethodName()
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Gets the attachment handler.
     *
     * @return the attachment handler
     */
    public ServerAttachmentHandler getAttachmentHandler() {
        return attachmentHandler;
    }
    

    /**
     * get the authenticationHandler
     * @return the authenticationHandler
     */
    public ServerAuthenticationDataHandler getAuthenticationHandler() {
        return authenticationHandler;
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

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getDescription()
     */
    public String getDescription() {
        return description;
    }
}
