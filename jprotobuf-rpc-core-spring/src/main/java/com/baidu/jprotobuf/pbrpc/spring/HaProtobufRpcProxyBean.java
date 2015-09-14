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

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationFactory;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.HaProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * To enhance {@link HaProtobufRpcProxy} to process extra parameters transportation.
 * 
 * @author xiemalin
 * @since 2.17
 */
public class HaProtobufRpcProxyBean<T> extends HaProtobufRpcProxy<T> {
    private static final ThreadLocal<MethodInvocation> CURRENT_PARAMS = new ThreadLocal<MethodInvocation>();

    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

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
     * @see com.baidu.jprotobuf.pbrpc.client.ha.HaProtobufRpcProxy#onBuildProtobufRpcProxy(com.baidu.jprotobuf.pbrpc.
     * transport.RpcClient, java.lang.Class)
     */
    @Override
    protected ProtobufRpcProxy<T> onBuildProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass) {
        ProtobufRpcProxyBean<T> protobufRpcProxyBean = new ProtobufRpcProxyBean<T>(rpcClient, interfaceClass);
        protobufRpcProxyBean.setRemoteInvocationFactory(remoteInvocationFactory);
        return protobufRpcProxyBean;
    }

    /**
     * @param rpcClient
     * @param interfaceClass
     */
    public HaProtobufRpcProxyBean(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService) {
        super(rpcClient, interfaceClass, namingService);
    }
    
    public HaProtobufRpcProxyBean(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService,
            NamingServiceLoadBalanceStrategyFactory loadBalanceStrategyFactory,
            FailOverInterceptor failOverInterceptor) {
        super(rpcClient, interfaceClass, namingService, loadBalanceStrategyFactory, failOverInterceptor);
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
            return super.invoke(invocation);
        } finally {
            CURRENT_PARAMS.remove();
        }
    }
}
