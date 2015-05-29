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

package com.baidu.jprotobuf.pbrpc.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
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
 * @since 1.0
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
    
    private T instance;
    
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

    public synchronized T proxy() {
        
        if (instance != null) {
            return instance;
        }

        // to parse interface
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            ProtobufRPC protobufPRC = method.getAnnotation(ProtobufRPC.class);
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
        
        instance = ProxyFactory.createProxy(interfaceClass, this);
        return instance;
    }

    protected RpcDataPackage buildRequestDataPackage(RpcMethodInfo rpcMethodInfo, Object[] args) throws IOException {
        RpcDataPackage rpcDataPackage = RpcDataPackage.buildRpcDataPackage(rpcMethodInfo, args);
        return rpcDataPackage;
    }
    
    public void close() {
        if (rpcChannel != null) {
            rpcChannel.close();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ProtobufRPC protobufPRC = method.getAnnotation(ProtobufRPC.class);
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
        RpcDataPackage rpcDataPackage = buildRequestDataPackage(rpcMethodInfo, args);
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
