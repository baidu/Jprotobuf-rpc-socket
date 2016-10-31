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

import org.aopalliance.intercept.MethodInvocation;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.HaProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * To enhance {@link HaProtobufRpcProxy} to process extra parameters transportation.
 *
 * @author xiemalin
 * @param <T> the generic type
 * @since 2.17
 */
public class HaProtobufRpcProxyBean<T> extends HaProtobufRpcProxy<T> {
    
    /** The Constant CURRENT_PARAMS. */
    private static final ThreadLocal<MethodInvocation> CURRENT_PARAMS = new ThreadLocal<MethodInvocation>();

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.HaProtobufRpcProxy#onBuildProtobufRpcProxy(com.baidu.jprotobuf.pbrpc.
     * transport.RpcClient, java.lang.Class)
     */
    @Override
    protected ProtobufRpcProxy<T> onBuildProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass) {
        ProtobufRpcProxyBean<T> protobufRpcProxyBean = new ProtobufRpcProxyBean<T>(rpcClient, interfaceClass);
        return protobufRpcProxyBean;
    }

    /**
     * Instantiates a new ha protobuf rpc proxy bean.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
     * @param namingService the naming service
     */
    public HaProtobufRpcProxyBean(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService) {
        super(rpcClient, interfaceClass, namingService);
    }
    
    /**
     * Instantiates a new ha protobuf rpc proxy bean.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
     * @param namingService the naming service
     * @param loadBalanceStrategyFactory the load balance strategy factory
     * @param failOverInterceptor the fail over interceptor
     */
    public HaProtobufRpcProxyBean(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService,
            NamingServiceLoadBalanceStrategyFactory loadBalanceStrategyFactory,
            SocketFailOverInterceptor failOverInterceptor) {
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
