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
package com.baidu.jprotobuf.pbrpc.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * To enhance {@link ProtobufRpcProxy} to process extra parameters transportation.
 *
 * @author xiemalin
 * @param <T> the generic type
 * @since 2.17
 */
public class ProtobufRpcProxyBean<T> extends ProtobufRpcProxy<T> implements MethodInterceptor {

    /** The Constant CURRENT_PARAMS. */
    private static final ThreadLocal<MethodInvocation> CURRENT_PARAMS = new ThreadLocal<MethodInvocation>();

    /** The proxy bean. */
    private Object proxyBean;

    /**
     * Sets the proxy bean.
     *
     * @param proxyBean the new proxy bean
     */
    protected void setProxyBean(Object proxyBean) {
        this.proxyBean = proxyBean;
    }


    /**
     * Instantiates a new protobuf rpc proxy bean.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
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
