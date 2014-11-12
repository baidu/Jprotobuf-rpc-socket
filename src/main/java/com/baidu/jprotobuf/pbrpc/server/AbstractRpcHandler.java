/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

import java.lang.reflect.Method;

import com.baidu.jprotobuf.pbrpc.ProtobufPRCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.utils.ReflectionUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * default RPC handler for google protobuf generated code.
 * 
 * @author xiemalin
 * @since 1.0
 * @see RpcServiceRegistry
 */
public abstract class AbstractRpcHandler implements RpcHandler {

    private String serviceName;
    private String methodName;
    private Method method;
    private ProtobufPRCService protobufPRCService;
    private Class inputClass;
    private Class outputClass;
    private Object service;

    /**
     * get the method
     * 
     * @return the method
     */
    protected Method getMethod() {
        return method;
    }

    /**
     * get the service
     * 
     * @return the service
     */
    protected Object getService() {
        return service;
    }


    /**
     * @param method
     * @param service
     * @param protobufPRCService
     */
    public AbstractRpcHandler(Method method, Object service, ProtobufPRCService protobufPRCService) {
        super();
        this.method = method;
        this.service = service;
        this.protobufPRCService = protobufPRCService;
        
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
    protected String getMethodName() {
        return methodName;
    }

}
