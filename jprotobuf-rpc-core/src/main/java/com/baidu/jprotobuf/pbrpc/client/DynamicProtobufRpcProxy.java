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
import com.baidu.jprotobuf.pbrpc.AuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.CompressType;
import com.baidu.jprotobuf.pbrpc.DummyAuthenticationDataHandler;
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
import com.baidu.jprotobuf.pbrpc.utils.ServiceSignatureUtils;

/**
 * 增加动态代理功能，支持动态RPC调用能力. 相比较 {@link ProtobufRpcProxy}实现，无需要提供接口定义。
 *
 * @author xiemalin
 * @since 3.5.0
 */
public class DynamicProtobufRpcProxy {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(DynamicProtobufRpcProxy.class.getName());

    /** The rpc client. */
    private RpcClient rpcClient;

    /** The host. */
    private String host;

    /** The port. */
    private int port;

    /** The Constant NULL. */
    private static final Object NULL = new Object();

    /** The rpc channel map. */
    private Map<String, RpcChannel> rpcChannelMap = new HashMap<String, RpcChannel>();

    /** The rpc methods. */
    private Map<String, RpcMethodInfo> rpcMethods = new HashMap<String, RpcMethodInfo>();

    /** The Constant EMPTY_CONFIG. */
    private static final Map<String, String> EMPTY_CONFIG = Collections.emptyMap();

    /** The Constant TIMEOUT_KEY. */
    public static final String TIMEOUT_KEY = "TIME_OUT";

    /**
     * Instantiates a new dynamic protobuf rpc proxy.
     *
     * @param rpcClient RPC client
     */
    public DynamicProtobufRpcProxy(RpcClient rpcClient) {
        super();
        this.rpcClient = rpcClient;

        if (rpcClient == null) {
            throw new IllegalArgumentException("Param 'rpcClient'  is null.");
        }
    }

    /**
     * Invoke.
     *
     * @param serviceSignature the service signature
     * @param proxy the proxy
     * @param method the method
     * @param args the args
     * @param cls the cls
     * @return the object
     * @throws Throwable the throwable
     */
    public Object invoke(final String serviceSignature, Object proxy, final Method method, final Object[] args,
            final Class<? extends ClientAttachmentHandler> cls) throws Throwable {
        return invoke(serviceSignature, proxy, method, args, EMPTY_CONFIG, cls);
    }

    /**
     * Gets the timeout.
     *
     * @param config the config
     * @return the timeout
     */
    private long getTimeout(final Map<String, String> config) {
        return getLong(config, TIMEOUT_KEY);

    }

    /**
     * Gets the long.
     *
     * @param config the config
     * @param key the key
     * @return the long
     */
    private long getLong(final Map<String, String> config, String key) {
        if (config == null) {
            return 0L;
        }

        String value = config.get(key);
        return StringUtils.toLong(value, 0L);
    }

    /**
     * Invoke.
     *
     * @param serviceSignature the service signature
     * @param proxy the proxy
     * @param method the method
     * @param args the args
     * @param config the config
     * @param cls the cls
     * @return the object
     * @throws Throwable the throwable
     */
    public Object invoke(final String serviceSignature, Object proxy, final Method method, final Object[] args,
            final Map<String, String> config, final Class<? extends ClientAttachmentHandler> cls) throws Throwable {

        return invoke(Constants.DYNAMIC_SERVICE_NAME, serviceSignature, proxy, method, args, config, cls);
    }
    
    public Object invoke(final String serviceName, final String methodName, Object proxy, final Method method,
            final Object[] args, final Map<String, String> config, final Class<? extends ClientAttachmentHandler> cls)
                    throws Throwable {
        return invoke(serviceName, methodName, proxy, method, args, config, cls, DummyAuthenticationDataHandler.class,
                DummyLogIDGenerator.class);
    }

    /**
     * Invoke.
     *
     * @param serviceSignature the service signature
     * @param proxy the proxy
     * @param method the method
     * @param args the args
     * @param config the config
     * @param cls the cls
     * @return the object
     * @throws Throwable the throwable
     */
    public Object invoke(final String serviceName, final String methodName, Object proxy, final Method method,
            final Object[] args, final Map<String, String> config, final Class<? extends ClientAttachmentHandler> cls,
            final Class<? extends AuthenticationDataHandler> authenticationDataHandlerCls,
            final Class<? extends LogIDGenerator> logIdGenerateorCls)
                    throws Throwable {
        String serviceSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
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
                        return serviceName;
                    }

                    @Override
                    public long onceTalkTimeout() {
                        return getTimeout(config);
                    }

                    @Override
                    public String methodName() {
                        return methodName;
                    }

                    @Override
                    public Class<? extends LogIDGenerator> logIDGenerator() {
                        return logIdGenerateorCls;
                    }

                    @Override
                    public CompressType compressType() {
                        return CompressType.NO;
                    }

                    @Override
                    public Class<? extends ClientAttachmentHandler> attachmentHandler() {
                        return cls;
                    }

                    @Override
                    public Class<? extends AuthenticationDataHandler> authenticationDataHandler() {
                        return authenticationDataHandlerCls;
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
        return doInvoke(serviceSignature, rpcChannel, proxy, rpcMethodInfo, method, args);
    }

    /**
     * Do invoke.
     *
     * @param serviceSignature the service signature
     * @param rpcChannel the rpc channel
     * @param proxy the proxy
     * @param rpcMethodInfo the rpc method info
     * @param method the method
     * @param args the args
     * @return the object
     * @throws Throwable the throwable
     */
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

    /**
     * Builds the request data package.
     *
     * @param rpcMethodInfo the rpc method info
     * @param args the args
     * @return the rpc data package
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected RpcDataPackage buildRequestDataPackage(RpcMethodInfo rpcMethodInfo, Object[] args) throws IOException {
        RpcDataPackage rpcDataPackage = RpcDataPackage.buildRpcDataPackage(rpcMethodInfo, args);
        return rpcDataPackage;
    }

    /**
     * do wait {@link BlockingRpcCallback} return.
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

    /**
     * Process equals hash code to string method.
     *
     * @param serviceSignature the service signature
     * @param method the method
     * @param args the args
     * @return the object
     */
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
     * Sets the host.
     *
     * @param host the new host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Close.
     */
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
