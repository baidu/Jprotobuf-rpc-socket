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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.bjf.remoting.protobuf.utils.StringUtils;
import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.CompressType;
import com.baidu.jprotobuf.pbrpc.DummyLogIDGenerator;
import com.baidu.jprotobuf.pbrpc.ErrorDataException;
import com.baidu.jprotobuf.pbrpc.LogIDGenerator;
import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcResponseMeta;
import com.baidu.jprotobuf.pbrpc.transport.BlockingRpcCallback;
import com.baidu.jprotobuf.pbrpc.transport.Connection;
import com.baidu.jprotobuf.pbrpc.transport.RpcChannel;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.handler.ErrorCodes;
import com.baidu.jprotobuf.pbrpc.utils.Constants;

/**
 * 增加动态代理功能，支持动态RPC调用能力. 相比较 {@link ProtobufRpcProxy}实现，无需要提供接口定义。
 *
 * @author xiemalin
 * @since 3.5.0
 */
public class DynamicProtobufRpcProxy {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(DynamicProtobufRpcProxy.class.getName());

    private RpcClient rpcClient;

    private String host;

    private int port;

    private static final Object NULL = new Object();

    private Map<String, RpcChannel> rpcChannelMap = new HashMap<String, RpcChannel>();

    private Map<String, RpcMethodInfo> rpcMethods = new HashMap<String, RpcMethodInfo>();

    private static final Map<String, String> EMPTY_CONFIG = Collections.emptyMap();

    public static final String TIMEOUT_KEY = "TIME_OUT";

    /**
     * @param rpcClient RPC client
     */
    public DynamicProtobufRpcProxy(RpcClient rpcClient) {
        super();
        this.rpcClient = rpcClient;

        if (rpcClient == null) {
            throw new IllegalArgumentException("Param 'rpcClient'  is null.");
        }
    }

    public Object invoke(final String serviceSignature, Object proxy, final Method method, final Object[] args,
            final Class<? extends ClientAttachmentHandler> cls) throws Throwable {
        return invoke(serviceSignature, proxy, method, args, EMPTY_CONFIG, cls);
    }

    private long getTimeout(final Map<String, String> config) {
        return getLong(config, TIMEOUT_KEY);

    }

    private long getLong(final Map<String, String> config, String key) {
        if (config == null) {
            return 0L;
        }

        String value = config.get(key);
        return StringUtils.toLong(value, 0L);
    }

    public Object invoke(final String serviceSignature, Object proxy, final Method method, final Object[] args,
            final Map<String, String> config, final Class<? extends ClientAttachmentHandler> cls) throws Throwable {

        Object result = processEqualsHashCodeToStringMethod(serviceSignature, method, args);
        if (result != NULL) {
            return result;
        }

        RpcMethodInfo rpcMethodInfo = rpcMethods.get(serviceSignature);
        if (rpcMethodInfo == null) {
            synchronized (serviceSignature) {
                ProtobufRPC protobufPRC = new ProtobufRPC() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return ProtobufRPC.class;
                    }

                    @Override
                    public String serviceName() {
                        return Constants.DYNAMIC_SERVICE_NAME;
                    }

                    @Override
                    public long onceTalkTimeout() {
                        return getTimeout(config);
                    }

                    @Override
                    public String methodName() {
                        return serviceSignature;
                    }

                    @Override
                    public Class<? extends LogIDGenerator> logIDGenerator() {
                        return DummyLogIDGenerator.class;
                    }

                    @Override
                    public CompressType compressType() {
                        return CompressType.NO;
                    }

                    @Override
                    public Class<? extends ClientAttachmentHandler> attachmentHandler() {
                        return cls;
                    }
                };

                RpcMethodInfo methodInfo;
                if (!RpcMethodInfo.isMessageType(method)) {
                    // using POJO
                    methodInfo = new PojoRpcMethodInfo(method, protobufPRC);

                } else {
                    // support google protobuf GeneratedMessage
                    methodInfo = new GeneratedMessageRpcMethodInfo(method, protobufPRC);
                }
                methodInfo.setOnceTalkTimeout(protobufPRC.onceTalkTimeout());
                methodInfo.setServiceName(protobufPRC.serviceName());
                methodInfo.setMethodName(protobufPRC.methodName());
                rpcMethodInfo = methodInfo;
                rpcMethods.put(serviceSignature, methodInfo);
            }
        }

        RpcChannel rpcChannel = rpcChannelMap.get(serviceSignature);

        if (rpcChannel == null) {

            synchronized (serviceSignature) {

                rpcChannel = new RpcChannel(rpcClient, host, port);
                rpcChannelMap.put(serviceSignature, rpcChannel);

            }

        }
        try {
            return doInvoke(serviceSignature, rpcChannel, proxy, rpcMethodInfo, method, args);
        } finally {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    protected Object doInvoke(final String serviceSignature, final RpcChannel rpcChannel, Object proxy,
            final RpcMethodInfo rpcMethodInfo, final Method method, final Object[] args) throws Throwable {

        if (rpcMethodInfo == null) {
            throw new IllegalAccessError(
                    "Can not invoke method '" + method.getName() + "' due to not a protbufRpc method.");
        }

        long onceTalkTimeout = rpcMethodInfo.getOnceTalkTimeout();
        if (onceTalkTimeout <= 0) {
            // use default once talk timeout
            onceTalkTimeout = rpcClient.getRpcClientOptions().getOnceTalkTimeout();
        }

        RpcDataPackage rpcDataPackage = buildRequestDataPackage(rpcMethodInfo, args);
        // set correlationId
        rpcDataPackage.getRpcMeta().setCorrelationId(rpcClient.getNextCorrelationId());

        if (rpcChannel == null) {
            throw new RuntimeException("No rpcChannel bind with serviceSignature '" + serviceSignature + "'");
        }

        final Connection connection = rpcChannel.getConnection();

        final BlockingRpcCallback callback = new BlockingRpcCallback(new BlockingRpcCallback.CallbackDone() {

            @Override
            public void done() {
                if (rpcChannel != null) {
                    rpcChannel.releaseConnection(connection);
                }
            }
        });

        rpcChannel.doTransport(connection, rpcDataPackage, callback, onceTalkTimeout);

        final String m = rpcMethodInfo.getMethodName();
        if (method.getReturnType().isAssignableFrom(Future.class)) {
            // if use non-blocking call
            Future<Object> f = new Future<Object>() {

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    // can not cancel
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return callback.isDone();
                }

                @Override
                public Object get() throws InterruptedException, ExecutionException {
                    try {
                        Object o = doWaitCallback(method, args, rpcMethodInfo.getServiceName(), m, rpcMethodInfo,
                                callback);
                        return o;
                    } catch (Exception e) {
                        throw new ExecutionException(e.getMessage(), e);
                    }
                }

                @Override
                public Object get(long timeout, TimeUnit unit)
                        throws InterruptedException, ExecutionException, TimeoutException {
                    return get();
                }
            };

            return f;
        }

        Object o = doWaitCallback(method, args, rpcMethodInfo.getServiceName(), rpcMethodInfo.getMethodName(),
                rpcMethodInfo, callback);
        return o;
    }

    protected RpcDataPackage buildRequestDataPackage(RpcMethodInfo rpcMethodInfo, Object[] args) throws IOException {
        RpcDataPackage rpcDataPackage = RpcDataPackage.buildRpcDataPackage(rpcMethodInfo, args);
        return rpcDataPackage;
    }

    /**
     * do wait {@link BlockingRpcCallback} return
     * 
     * @param method java method object
     * @param args method arguments
     * @param serviceName service name
     * @param methodName method name
     * @param rpcMethodInfo RPC method info
     * @param callback {@link BlockingRpcCallback} object
     * @return RPC result
     * @throws ErrorDataException in case of error data message from RPC service
     * @throws IOException in case of decode response from RPC failed
     */
    private Object doWaitCallback(Method method, Object[] args, String serviceName, String methodName,
            RpcMethodInfo rpcMethodInfo, BlockingRpcCallback callback) throws ErrorDataException, IOException {
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
                throw new ErrorDataException("A error occurred: errorCode=" + errorCode + " errorMessage:" + error,
                        errorCode);
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

        Object o = rpcMethodInfo.outputDecode(data);
        return o;
    }

    private Object processEqualsHashCodeToStringMethod(String serviceSignature, Method method, final Object[] args) {
        String name = method.getName();

        Object[] parameters = args;
        if (parameters == null) {
            parameters = new Object[0];
        }

        if ("toString".equals(name) && parameters.length == 0) {
            return serviceSignature;
        } else if ("hashCode".equals(name) && parameters.length == 0) {
            return serviceSignature.hashCode();
        } else if ("equals".equals(name) && parameters.length == 1) {
            return this.equals(parameters[0]);
        }

        return NULL;
    }

    /**
     * set host value to host
     * 
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * set port value to port
     * 
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    public void close() {
        Collection<RpcChannel> rpcChannels = rpcChannelMap.values();
        for (RpcChannel rpcChann : rpcChannels) {
            try {
                rpcChann.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e.getCause());
            }
        }

    }

}
