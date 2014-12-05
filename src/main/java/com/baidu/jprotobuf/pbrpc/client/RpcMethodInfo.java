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

import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.DummyClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.DummyLogIDGenerator;
import com.baidu.jprotobuf.pbrpc.LogIDGenerator;
import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.baidu.jprotobuf.pbrpc.utils.ReflectionUtils;
import com.google.protobuf.GeneratedMessage;

/**
 * RPC method description info.
 * 
 * @author xiemalin
 * @since 1.0
 * @see ProtobufRpcProxy
 * @see ProtobufRPC
 */
public abstract class RpcMethodInfo {

    private Method method;
    private ProtobufRPC protobufPRC;
    private String serviceName;
    private String methodName;
    private long onceTalkTimeout;

    private Class<? extends Object> inputClass;
    private Class<? extends Object> outputClass;

    private LogIDGenerator logIDGenerator;
    private ClientAttachmentHandler clientAttachmentHandler;

    public abstract byte[] inputEncode(Object input) throws IOException;

    public abstract Object outputDecode(byte[] output) throws IOException;

    public static boolean isMessageType(Method method) {

        boolean paramMessagetType = false;

        Class<?>[] types = method.getParameterTypes();
        if (types.length == 1) {
            if (GeneratedMessage.class.isAssignableFrom(types[0])) {
                paramMessagetType = true;
            }
        }

        Class<?> returnType = method.getReturnType();
        if (!ReflectionUtils.isVoid(returnType)) {
            if (GeneratedMessage.class.isAssignableFrom(returnType)) {
                if (paramMessagetType) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Invalid RPC method. parameter type and return "
                            + "type should define in same way.");
                }
            }
        }

        return false;
    }

    /**
     * @param method
     * @param protobufPRC
     */
    public RpcMethodInfo(Method method, ProtobufRPC protobufPRC) {
        this.method = method;
        this.protobufPRC = protobufPRC;

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

        // initialize others
        Class<? extends LogIDGenerator> logIDGeneratorClass = protobufPRC.logIDGenerator();
        if (logIDGeneratorClass != DummyLogIDGenerator.class) {
            try {
                logIDGenerator = logIDGeneratorClass.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'logIDGenerator' of class '"
                        + logIDGeneratorClass.getName() + "'");
            }
        }

        Class<? extends ClientAttachmentHandler> attachmentHandlerClass = protobufPRC.attachmentHandler();
        if (attachmentHandlerClass != DummyClientAttachmentHandler.class) {
            try {
                clientAttachmentHandler = attachmentHandlerClass.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'logIDGenerator' of class '"
                        + attachmentHandlerClass.getName() + "'");
            }
        }

    }

    /**
     * get the logIDGenerator
     * 
     * @return the logIDGenerator
     */
    public LogIDGenerator getLogIDGenerator() {
        return logIDGenerator;
    }

    /**
     * get the clientAttachmentHandler
     * 
     * @return the clientAttachmentHandler
     */
    public ClientAttachmentHandler getClientAttachmentHandler() {
        return clientAttachmentHandler;
    }

    /**
     * get the inputClass
     * 
     * @return the inputClass
     */
    public Class<? extends Object> getInputClass() {
        return inputClass;
    }

    /**
     * get the outputClass
     * 
     * @return the outputClass
     */
    public Class<? extends Object> getOutputClass() {
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
     * 
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * set methodName value to methodName
     * 
     * @param methodName
     *            the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * get the onceTalkTimeout
     * 
     * @return the onceTalkTimeout
     */
    public long getOnceTalkTimeout() {
        return onceTalkTimeout;
    }

    /**
     * set onceTalkTimeout value to onceTalkTimeout
     * 
     * @param onceTalkTimeout
     *            the onceTalkTimeout to set
     */
    public void setOnceTalkTimeout(long onceTalkTimeout) {
        this.onceTalkTimeout = onceTalkTimeout;
    }

    /**
     * get the method
     * 
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * get the protobufPRC
     * 
     * @return the protobufPRC
     */
    public ProtobufRPC getProtobufPRC() {
        return protobufPRC;
    }

}
