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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.DummyServerAuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.ServerAuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaServiceProvider;
import com.baidu.jprotobuf.pbrpc.utils.Constants;
import com.baidu.jprotobuf.pbrpc.utils.ReflectionUtils;
import com.baidu.jprotobuf.pbrpc.utils.ServiceSignatureUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * To register all RPC service.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServiceRegistry {

    /** log this class. */
    protected static final Logger LOGGER = Logger.getLogger(RpcServiceRegistry.class.getName());

    /**
     * registered service map. the key if unique represent service name
     */
    private Map<String, RpcHandler> serviceMap = new HashMap<String, RpcHandler>();

    /**
     * if override exist allowed. default is not allowed
     */
    private boolean dummyOverride = false;

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
     * default constructor.
     */
    public RpcServiceRegistry() {
    }

    /**
     * Do register meta service.
     */
    public void doRegisterMetaService() {
        RpcServiceMetaServiceProvider metaService = new RpcServiceMetaServiceProvider(this);
        registerService(metaService);
    }

    /**
     * Un register all.
     */
    public void unRegisterAll() {
        serviceMap.clear();
    }

    /**
     * Sets the if override exist allowed.
     *
     * @param dummyOverride the new if override exist allowed
     */
    public void setDummyOverride(boolean dummyOverride) {
        this.dummyOverride = dummyOverride;
    }

    /**
     * Register service.
     *
     * @param target the target
     */
    public void registerService(final Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Param 'target' is null.");
        }

        Class<? extends Object> cls = target.getClass();

        ReflectionUtils.doWithMethods(cls, new ReflectionUtils.MethodCallback() {

            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {

                ProtobufRPCService protobufPRCService = method.getAnnotation(ProtobufRPCService.class);
                if (protobufPRCService != null) {
                    doRegiterService(method, target, protobufPRCService);
                }

            }
        });

    }

    /**
     * Do create rpc handler.
     *
     * @param method the method
     * @param service the service
     * @param protobufPRCService the protobuf prc service
     * @return the rpc handler
     */
    protected RpcHandler doCreateRpcHandler(Method method, Object service, ProtobufRPCService protobufPRCService) {
        boolean messageType = RpcMethodInfo.isMessageType(method);
        AbstractAnnotationRpcHandler rpcHandler;
        if (!messageType) {
            rpcHandler = new AnnotationRpcHandler(method, service, protobufPRCService);
        } else {
            rpcHandler = new MessageGeneratedRpcHandler(method, service, protobufPRCService);
        }
        rpcHandler.setInterceptor(interceptor);
        if (StringUtils.isEmpty(rpcHandler.getServiceName())) {
            throw new IllegalArgumentException(" serviceName from 'serviceExporter' is empty.");
        }
        return rpcHandler;
    }

    /**
     * Do dynamic register service.
     *
     * @param methodSignature the method signature
     * @param method the method
     * @param service the service
     * @param cls the cls
     */
    public void doDynamicRegisterService(final String methodSignature, Method method, Object service,
            final Class<? extends ServerAttachmentHandler> cls) {
        doDynamicRegisterService(Constants.DYNAMIC_SERVICE_NAME, methodSignature, method, service, cls);
    }
    
    public void doDynamicRegisterService(final String serviceName, final String methodName, Method method,
            Object service, final Class<? extends ServerAttachmentHandler> cls) {
        doDynamicRegisterService(serviceName, methodName, method, 
                service, cls, DummyServerAuthenticationDataHandler.class);
    }

    /**
     * Do dynamic register service.
     *
     * @param serviceName the service name
     * @param methodName the method name
     * @param method the method
     * @param service the service
     * @param cls the cls
     */
    public void doDynamicRegisterService(final String serviceName, final String methodName, Method method,
            Object service, final Class<? extends ServerAttachmentHandler> cls, 
            final Class<? extends ServerAuthenticationDataHandler> authentiationDataCls) {
        ProtobufRPCService protobufPRCService = new ProtobufRPCService() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ProtobufRPCService.class;
            }

            @Override
            public String serviceName() {
                return serviceName;
            }

            @Override
            public String methodName() {
                return methodName;
            }

            @Override
            public String description() {
                return "";
            }

            @Override
            public Class<? extends ServerAttachmentHandler> attachmentHandler() {
                return cls;
            }

            @Override
            public Class<? extends ServerAuthenticationDataHandler> authenticationDataHandler() {
                return authentiationDataCls;
            }
        };

        doRegiterService(method, service, protobufPRCService);
    }

    /**
     * Do regiter service.
     *
     * @param method the method
     * @param service the service
     * @param protobufPRCService the protobuf prc service
     */
    protected void doRegiterService(Method method, Object service, ProtobufRPCService protobufPRCService) {
        RpcHandler rpcHandler = doCreateRpcHandler(method, service, protobufPRCService);
        String methodSignature = rpcHandler.getMethodSignature();

        if (serviceMap.containsKey(methodSignature)) {
            if (dummyOverride) {
                serviceMap.put(methodSignature, rpcHandler);
            } else {
                throw new RuntimeException("serviceName '" + rpcHandler.getServiceName() + " ' and methodName '"
                        + method.getName() + "' aready exist.");
            }
        } else {
            serviceMap.put(methodSignature, rpcHandler);
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("RPC service register log: serviceName[" + rpcHandler.getServiceName() + "] methodName["
                    + rpcHandler.getMethodName() + "] from " + method.getDeclaringClass().getName() + "."
                    + method.getName());
        }

    }

    /**
     * Gets the method signature.
     *
     * @param serviceName the service name
     * @param method the method
     * @return the method signature
     */
    private String getMethodSignature(String serviceName, String method) {
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, method);
        return methodSignature;
    }

    /**
     * Lookup service.
     *
     * @param serviceName the service name
     * @param methodName the method name
     * @return the rpc handler
     */
    public RpcHandler lookupService(String serviceName, String methodName) {
        String methodSignature = getMethodSignature(serviceName, methodName);
        return serviceMap.get(methodSignature);
    }

    /**
     * Gets the services.
     *
     * @return the services
     */
    public Collection<RpcHandler> getServices() {
        return serviceMap.values();
    }

    /**
     * Register service.
     *
     * @param serviceExporter the service exporter
     */
    public void registerService(IDLServiceExporter serviceExporter) {
        if (serviceExporter == null) {
            throw new IllegalArgumentException("Param 'serviceExporter' is null.");
        }

        String serviceName = serviceExporter.getServiceName();
        if (StringUtils.isEmpty(serviceName)) {
            throw new IllegalArgumentException(" serviceName from 'serviceExporter' is empty.");
        }

        String methodSignature = getMethodSignature(serviceName, serviceExporter.getMethodName());

        if (serviceMap.containsKey(methodSignature)) {
            if (dummyOverride) {
                serviceMap.put(methodSignature, new IDLServiceRpcHandler(serviceExporter));
            } else {
                throw new RuntimeException("serviceName '" + serviceName + " ' and methodName '"
                        + serviceExporter.getMethodName() + "' aready exist.");
            }
        } else {
            serviceMap.put(methodSignature, new IDLServiceRpcHandler(serviceExporter));
        }

    }
    
    /**
     * remove service by service name and method name
     * @param methodName method name 
     */
    public void unRegisterDynamicService(String methodName) {
        String methodSignature = ServiceSignatureUtils.makeSignature(Constants.DYNAMIC_SERVICE_NAME, methodName);
        serviceMap.remove(methodSignature);
    }

    /**
     * remove service by service name and method name
     * @param serviceName service name
     * @param methodName method name 
     */
    public void unRegisterDynamicService(String serviceName, String methodName) {
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
        serviceMap.remove(methodSignature);
    }
}
