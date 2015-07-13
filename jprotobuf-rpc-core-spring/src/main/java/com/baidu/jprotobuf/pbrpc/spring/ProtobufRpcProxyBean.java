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
package com.baidu.jprotobuf.pbrpc.spring;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.util.SerializationUtils;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.RpcMethodInfo;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * To enhance {@link ProtobufRpcProxy} to process extra parameters transportation.
 * 
 * @author xiemalin
 * @since 2.17
 */
public class ProtobufRpcProxyBean<T> extends ProtobufRpcProxy<T> implements MethodInterceptor {

    private static final ThreadLocal<MethodInvocation> CURRENT_PARAMS = new ThreadLocal<MethodInvocation>();

    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

    private Object proxyBean;

    /**
     * set proxyBean value to proxyBean
     * 
     * @param proxyBean the proxyBean to set
     */
    protected void setProxyBean(Object proxyBean) {
        this.proxyBean = proxyBean;
    }

    /**
     * get the remoteInvocationFactory
     * 
     * @return the remoteInvocationFactory
     */
    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return remoteInvocationFactory;
    }

    /**
     * set remoteInvocationFactory value to remoteInvocationFactory
     * 
     * @param remoteInvocationFactory the remoteInvocationFactory to set
     */
    public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory =
                (remoteInvocationFactory != null ? remoteInvocationFactory : new DefaultRemoteInvocationFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy#buildRequestDataPackage(com.baidu.jprotobuf.pbrpc.client.
     * RpcMethodInfo, java.lang.Object[])
     */
    @Override
    protected RpcDataPackage buildRequestDataPackage(RpcMethodInfo rpcMethodInfo, Object[] args) throws IOException {
        RpcDataPackage rpcDataPackage = super.buildRequestDataPackage(rpcMethodInfo, args);

        MethodInvocation methodInvocation = CURRENT_PARAMS.get();
        if (methodInvocation != null) {
            RemoteInvocation ri = remoteInvocationFactory.createRemoteInvocation(methodInvocation);
            Map<String, Serializable> map = ri.getAttributes();

            if (map != null) {
                byte[] data = SerializationUtils.serialize(map);
                rpcDataPackage.extraParams(data);
            }
        }

        return rpcDataPackage;
    }

    /**
     * @param rpcClient
     * @param interfaceClass
     */
    public ProtobufRpcProxyBean(RpcClient rpcClient, Class<T> interfaceClass) {
        super(rpcClient, interfaceClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        CURRENT_PARAMS.set(invocation);
        try {
            if (proxyBean == null) {
                proxyBean = proxy();
            }

            Object result = invoke(proxyBean, invocation.getMethod(), invocation.getArguments());
            return result;
        } finally {
            CURRENT_PARAMS.remove();
        }
    }

}
