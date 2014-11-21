/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.ProtobufPRC;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcResponseMeta;
import com.baidu.jprotobuf.pbrpc.transport.BlockingRpcCallback;
import com.baidu.jprotobuf.pbrpc.transport.RpcChannel;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.handler.ErrorCodes;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * Protobuf RPC proxy utility class.
 * 
 * @author xiemalin
 * @since 1.o
 * @see ProxyFactory
 */
public class ProtobufRpcProxy<T> implements InvocationHandler {

    private Map<String, RpcMethodInfo> cachedRpcMethods = new HashMap<String, RpcMethodInfo>();
    
    /**
     * RPC client.
     */
    private final RpcClient rpcClient;
    private RpcChannel rpcChannel;
    
    private String host;
    private int port;
    
    private boolean lookupStubOnStartup = true;
    
    /**
     * get the lookupStubOnStartup
     * @return the lookupStubOnStartup
     */
    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    /**
     * set lookupStubOnStartup value to lookupStubOnStartup
     * @param lookupStubOnStartup the lookupStubOnStartup to set
     */
    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    /**
     * set host value to host
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * set port value to port
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * target interface class
     */
    private final Class<T> interfaceClass;

    /**
     * @param rpcClient
     */
    public ProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        if (rpcClient == null) {
            throw new IllegalArgumentException("Param 'rpcClient'  is null.");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Param 'interfaceClass'  is null.");
        }
        this.rpcClient = rpcClient;
    }

    public T proxy() {

        // to parse interface
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            ProtobufPRC protobufPRC = method.getAnnotation(ProtobufPRC.class);
            if (protobufPRC != null) {
                String serviceName = protobufPRC.serviceName();
                String methodName = protobufPRC.methodName();
                if (StringUtils.isEmpty(methodName)) {
                    methodName = method.getName();
                }

                String methodSignature = serviceName + '!' + methodName;
                if (cachedRpcMethods.containsKey(methodSignature)) {
                    throw new IllegalArgumentException(
                            "Method with annotation ProtobufPRC already defined service name [" + serviceName
                                    + "] method name [" + methodName + "]");
                }
                
                RpcMethodInfo methodInfo;
                if (!RpcMethodInfo.isMessageType(method)) {
                    // using POJO
                    methodInfo = new PojoRpcMethodInfo(method, protobufPRC);
                    
                } else {
                    // support google protobuf GeneratedMessage
                    methodInfo = new GeneratedMessageRpcMethodInfo(method, protobufPRC);
                }
                methodInfo.setOnceTalkTimeout(protobufPRC.onceTalkTimeout());
                methodInfo.setServiceName(serviceName);
                methodInfo.setMethodName(methodName);
                
                cachedRpcMethods.put(methodSignature, methodInfo);
                
            }
        }
        
        // if not protobufRpc method defined throw exception
        if (cachedRpcMethods.isEmpty()) {
            throw new IllegalArgumentException("This no protobufRpc method in interface class:"
                    + interfaceClass.getName());
        }
        
        rpcChannel = new RpcChannel(rpcClient, host, port);
        if (lookupStubOnStartup) {
            rpcChannel.testChannlConnect();
        }
        T proxy = ProxyFactory.createProxy(interfaceClass, this);
        return proxy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ProtobufPRC protobufPRC = method.getAnnotation(ProtobufPRC.class);
        if (protobufPRC == null) {
            throw new IllegalAccessError("Target method is not marked annotation @ProtobufPRC. method name :"
                    + method.getDeclaringClass().getName() + "." + method.getName());
        }

        String serviceName = protobufPRC.serviceName();
        String methodName = protobufPRC.methodName();
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }
        String methodSignature = serviceName + '!' + methodName;
        RpcMethodInfo rpcMethodInfo = cachedRpcMethods.get(methodSignature);
        if (rpcMethodInfo == null) {
            throw new IllegalAccessError("Can not invoke method '" + method.getName() 
                    + "' due to not a protbufRpc method.");
        }
        
        long onceTalkTimeout = rpcMethodInfo.getOnceTalkTimeout();
        if (onceTalkTimeout <= 0) {
            // use default once talk timeout
            onceTalkTimeout = rpcClient.getRpcClientOptions().getOnceTalkTimeout();
        }
        
        BlockingRpcCallback callback = new BlockingRpcCallback();
        RpcDataPackage rpcDataPackage = RpcDataPackage.buildRpcDataPackage(rpcMethodInfo, args);
        // set correlationId
        rpcDataPackage.getRpcMeta().setCorrelationId(rpcClient.getNextCorrelationId());
        rpcChannel.doTransport(rpcDataPackage, callback, onceTalkTimeout);
        
        if (!callback.isDone()) {
            synchronized (callback) {
                while (!callback.isDone()) {
                    try {
                        callback.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        RpcDataPackage message = callback.getMessage();
        
        RpcResponseMeta response = message.getRpcMeta().getResponse();
        if (response != null) {
            Integer errorCode = response.getErrorCode();
            if (!ErrorCodes.isSuccess(errorCode)) {
                String error = message.getRpcMeta().getResponse().getErrorText();
                throw new Throwable("A error occurred: errorCode=" + errorCode + " errorMessage:" + error);
            }
        }
        
        
        byte[] attachment = message.getAttachment();
        if (attachment != null) {
            ClientAttachmentHandler attachmentHandler = rpcMethodInfo.getClientAttachmentHandler();
            if (attachmentHandler != null) {
                attachmentHandler.handleResponse(attachment, serviceName, methodName, args);
            }
        }
        
        // handle response data
        byte[] data = message.getData();
        if (data == null) {
            return null;
        }
        
        return rpcMethodInfo.outputDecode(data);
    }
    

}
