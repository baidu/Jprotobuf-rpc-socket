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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.ErrorDataException;
import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcResponseMeta;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.intercept.MethodInvocationInfo;
import com.baidu.jprotobuf.pbrpc.transport.BlockingRpcCallback;
import com.baidu.jprotobuf.pbrpc.transport.Connection;
import com.baidu.jprotobuf.pbrpc.transport.RpcChannel;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.handler.ErrorCodes;
import com.baidu.jprotobuf.pbrpc.utils.ServiceSignatureUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * Protobuf RPC proxy utility class.
 *
 * @author xiemalin
 * @param <T> the generic type
 * @see ProxyFactory
 * @since 1.0
 */
public class ProtobufRpcProxy<T> implements InvocationHandler {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ProtobufRpcProxy.class.getName());

    /** The Constant NULL. */
    private static final Object NULL = new Object();

    /** Logger for this class. */
    private static final Logger PERFORMANCE_LOGGER = Logger.getLogger("performance-log");

    /**
     * key name for shared RPC channel.
     *
     * @see RpcChannel
     */
    private static final String SHARE_KEY = "___share_key";

    /** The cached rpc methods. */
    private Map<String, RpcMethodInfo> cachedRpcMethods = new HashMap<String, RpcMethodInfo>();

    /**
     * RPC client.
     */
    private final RpcClient rpcClient;

    /** The rpc channel map. */
    private Map<String, RpcChannel> rpcChannelMap = new HashMap<String, RpcChannel>();

    /** The host. */
    private String host;

    /** The port. */
    private int port;

    /** The lookup stub on startup. */
    private boolean lookupStubOnStartup = true;

    /** The instance. */
    private T instance;

    /** The service locator callback. */
    private ServiceLocatorCallback serviceLocatorCallback;

    /** The service url. */
    private String serviceUrl;

    /** The interceptor. */
    private InvokerInterceptor interceptor;

    /**
     * Sets the interceptor.
     *
     * @param interceptor the new interceptor
     */
    public void setInterceptor(InvokerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * Sets the service locator callback.
     *
     * @param serviceLocatorCallback the new service locator callback
     */
    public void setServiceLocatorCallback(ServiceLocatorCallback serviceLocatorCallback) {
        this.serviceLocatorCallback = serviceLocatorCallback;
    }

    /**
     * Checks if is lookup stub on startup.
     *
     * @return the lookup stub on startup
     */
    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    /**
     * Sets the lookup stub on startup.
     *
     * @param lookupStubOnStartup the new lookup stub on startup
     */
    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
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
     * Gets the service signatures.
     *
     * @return the service signatures
     */
    public Set<String> getServiceSignatures() {
        if (!cachedRpcMethods.isEmpty()) {
            return new HashSet<String>(cachedRpcMethods.keySet());
        }

        Set<String> serviceSignatures = new HashSet<String>();
        Method[] methods = interfaceClass.getMethods();
        for (Method method : methods) {
            ProtobufRPC protobufPRC = method.getAnnotation(ProtobufRPC.class);
            if (protobufPRC != null) {
                String serviceName = protobufPRC.serviceName();
                String methodName = protobufPRC.methodName();
                if (StringUtils.isEmpty(methodName)) {
                    methodName = method.getName();
                }

                String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
                serviceSignatures.add(methodSignature);
            }
        }
        // if not protobufRpc method defined throw exception
        if (serviceSignatures.isEmpty()) {
            throw new IllegalArgumentException(
                    "This no protobufRpc method in interface class:" + interfaceClass.getName());
        }
        return serviceSignatures;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /** target interface class. */
    private final Class<T> interfaceClass;

    /**
     * Instantiates a new protobuf rpc proxy.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
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

    /**
     * Gets the methds.
     *
     * @return the methds
     */
    protected Method[] getMethds() {
        return interfaceClass.getMethods();
    }

    /**
     * Proxy.
     *
     * @return the t
     */
    public synchronized T proxy() {

        if (instance != null) {
            return instance;
        }

        // to parse interface
        Method[] methods = getMethds();
        for (Method method : methods) {
            ProtobufRPC protobufPRC = getProtobufRPCAnnotation(method);
            if (protobufPRC != null) {
                String serviceName = protobufPRC.serviceName();
                String methodName = protobufPRC.methodName();
                if (StringUtils.isEmpty(methodName)) {
                    methodName = method.getName();
                }

                String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
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

                // do create rpc channal
                String eHost = host;
                int ePort = port;
                if (serviceLocatorCallback != null) {
                    InetSocketAddress address = serviceLocatorCallback.fetchAddress(methodSignature);
                    if (address == null) {
                        throw new RuntimeException("fetch a null address from serviceLocatorCallback"
                                + " by serviceSignature '" + methodSignature + "'");
                    }
                    eHost = address.getHostName();
                    ePort = address.getPort();
                }

                String channelKey = methodSignature;

                if (rpcClient.getRpcClientOptions().isShareThreadPoolUnderEachProxy()) {
                    channelKey = SHARE_KEY;
                }

                if (!rpcChannelMap.containsKey(channelKey)) {
                    RpcChannel rpcChannel = new RpcChannel(rpcClient, eHost, ePort);
                    if (lookupStubOnStartup) {
                        rpcChannel.testChannlConnect();
                    }

                    rpcChannelMap.put(channelKey, rpcChannel);
                }

                serviceUrl = eHost + ":" + ePort;
            }
        }

        // if not protobufRpc method defined throw exception
        if (cachedRpcMethods.isEmpty()) {
            throw new IllegalArgumentException(
                    "This no protobufRpc method in interface class:" + interfaceClass.getName());
        }

        Class[] clazz = { interfaceClass, ServiceUrlAccessible.class };
        instance = ProxyFactory.createProxy(clazz, interfaceClass.getClassLoader(), this);
        return instance;
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

    /**
     * Process equals hash code to string method.
     *
     * @param method the method
     * @param args the args
     * @return the object
     */
    private Object processEqualsHashCodeToStringMethod(Method method, final Object[] args) {
        String name = method.getName();

        Object[] parameters = args;
        if (parameters == null) {
            parameters = new Object[0];
        }

        if ("toString".equals(name) && parameters.length == 0) {
            return serviceUrl;
        } else if ("hashCode".equals(name) && parameters.length == 0) {
            return serviceUrl.hashCode();
        } else if ("equals".equals(name) && parameters.length == 1) {
            return this.equals(parameters[0]);
        }

        return NULL;
    }

    /**
     * Gets the protobuf rpc annotation.
     *
     * @param method the method
     * @return the protobuf rpc annotation
     */
    protected ProtobufRPC getProtobufRPCAnnotation(Method method) {
        ProtobufRPC protobufPRC = method.getAnnotation(ProtobufRPC.class);
        return protobufPRC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {

        String mName = method.getName();
        if ("getServiceUrl".equals(mName)) {

            // return directly from local due to call ServiceUrlAccessible
            return serviceUrl;
        }

        Object result = processEqualsHashCodeToStringMethod(method, args);
        if (result != NULL) {
            return result;
        }

        final long time = System.currentTimeMillis();

        ProtobufRPC protobufPRC = getProtobufRPCAnnotation(method);
        if (protobufPRC == null) {
            throw new IllegalAccessError("Target method is not marked annotation @ProtobufPRC. method name :"
                    + method.getDeclaringClass().getName() + "." + method.getName());
        }

        final String serviceName = protobufPRC.serviceName();
        String methodName = protobufPRC.methodName();
        if (StringUtils.isEmpty(methodName)) {
            methodName = mName;
        }
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
        final RpcMethodInfo rpcMethodInfo = cachedRpcMethods.get(methodSignature);
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

        String channelKey = methodSignature;
        if (rpcClient.getRpcClientOptions().isShareThreadPoolUnderEachProxy()) {
            channelKey = SHARE_KEY;
        }

        try {
            // check intercepter
            if (interceptor != null) {

                byte[] extraParams = rpcDataPackage.getRpcMeta().getRequest().getExtraParam();
                MethodInvocationInfo methodInvocationInfo = new MethodInvocationInfo(proxy, args, method, extraParams);
                interceptor.beforeInvoke(methodInvocationInfo);

                Object ret = interceptor.process(methodInvocationInfo);
                if (ret != null) {
                    PERFORMANCE_LOGGER.fine("RPC client invoke method(by intercepter) '" + method.getName()
                            + "' time took:" + (System.currentTimeMillis() - time) + " ms");
                    return ret;
                }

                rpcDataPackage.extraParams(methodInvocationInfo.getExtraParams());
            }

            final RpcChannel rpcChannel = rpcChannelMap.get(channelKey);
            if (rpcChannel == null) {
                throw new RuntimeException("No rpcChannel bind with serviceSignature '" + channelKey + "'");
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

            final String m = methodName;
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
                            Object o = doWaitCallback(method, args, serviceName, m, rpcMethodInfo, callback);
                            PERFORMANCE_LOGGER.fine("RPC client invoke method '" + method.getName() + "' time took:"
                                    + (System.currentTimeMillis() - time) + " ms");
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

            Object o = doWaitCallback(method, args, serviceName, methodName, rpcMethodInfo, callback);

            PERFORMANCE_LOGGER.fine("RPC client invoke method '" + method.getName() + "' time took:"
                    + (System.currentTimeMillis() - time) + " ms");
            return o;
        } finally {
            if (interceptor != null) {
                interceptor.afterProcess();
            }
        }
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

}
