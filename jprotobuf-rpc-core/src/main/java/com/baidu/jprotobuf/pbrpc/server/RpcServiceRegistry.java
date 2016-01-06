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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaServiceProvider;
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

    /**
     * log this class
     */
    protected static final Logger LOGGER = Logger.getLogger(RpcServiceRegistry.class.getName());

    /**
     * registered service map. the key if unique represent service name
     */
    private Map<String, RpcHandler> serviceMap = new HashMap<String, RpcHandler>();

    /**
     * if override exist allowed. default is not allowed
     */
    private boolean dummyOverride = false;
    
	private InvokerInterceptor interceptor;

	/**
	 * set interceptor value to interceptor
	 * 
	 * @param interceptor
	 *            the interceptor to set
	 */
	public void setInterceptor(InvokerInterceptor interceptor) {
		this.interceptor = interceptor;
	}

    /**
     * default constructor
     */
    public RpcServiceRegistry() {
    }

    public void doRegisterMetaService() {
        RpcServiceMetaServiceProvider metaService = new RpcServiceMetaServiceProvider(this);
        registerService(metaService);
    }

    public void unRegisterAll() {
        serviceMap.clear();
    }

    /**
     * set dummyOverride value to dummyOverride
     * 
     * @param dummyOverride the dummyOverride to set
     */
    public void setDummyOverride(boolean dummyOverride) {
        this.dummyOverride = dummyOverride;
    }

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

    private void doRegiterService(Method method, Object service, ProtobufRPCService protobufPRCService) {
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

    private String getMethodSignature(String serviceName, String method) {
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, method);
        return methodSignature;
    }

    public RpcHandler lookupService(String serviceName, String methodName) {
        String methodSignature = getMethodSignature(serviceName, methodName);
        return serviceMap.get(methodSignature);
    }

    public Collection<RpcHandler> getServices() {
        return serviceMap.values();
    }

    /**
     * @param serviceExporter
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
}
