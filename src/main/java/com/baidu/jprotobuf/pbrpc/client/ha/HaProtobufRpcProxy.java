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
package com.baidu.jprotobuf.pbrpc.client.ha;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.RoundRobinLoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * A enhanced {@link ProtobufRpcProxy} supports naming service and load blanace.
 * 
 * 
 * @author xiemalin
 * @since 2.16
 */
public class HaProtobufRpcProxy<T> {

    private final RpcClient rpcClient;
    private final Class<T> interfaceClass;
    private final NamingService namingService;
    private LoadBalanceStrategy loadBalanceStrategy;
    private FailOverInterceptor failOverInterceptor;
    private LoadBalanceProxyFactoryBean lbProxyBean;
    
    public HaProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService) {
        this(rpcClient, interfaceClass, namingService, null, null);
    }

    public HaProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService,
            LoadBalanceStrategy loadBalanceStrategy, FailOverInterceptor failOverInterceptor) {
        this.rpcClient = rpcClient;
        this.interfaceClass = interfaceClass;
        this.namingService = namingService;
        this.loadBalanceStrategy = loadBalanceStrategy;
        this.failOverInterceptor = failOverInterceptor;
        if (namingService == null) {
            throw new NullPointerException("param 'namingService' is null.");
        }

    }

    public T proxy() throws Exception {
        // get server list from NamingService
        List<InetSocketAddress> servers = namingService.list();
        
        if (CollectionUtils.isEmpty(servers)) {
            throw new RuntimeException("Can not proxy rpc client due to get a blank server list from namingService.");
        }
        
        lbProxyBean = new LoadBalanceProxyFactoryBean();
        lbProxyBean.setServiceInterface(interfaceClass);
        
        Map<String, String> serverUrls = new HashMap<String, String>(servers.size());
        Map<String, Object> targetBeans = new HashMap<String, Object>();
        for (InetSocketAddress address : servers) {
            String serviceUrl = address.getHostName() + ":" + address.getPort();
            serverUrls.put(serviceUrl, serviceUrl);
            
            ProtobufRpcProxy<T> protobufRpcProxy = new ProtobufRpcProxy<T>(rpcClient, interfaceClass);
            protobufRpcProxy.setHost(address.getHostName());
            protobufRpcProxy.setPort(address.getPort());
            
            T rpc = protobufRpcProxy.proxy();
            
            targetBeans.put(serviceUrl, rpc);
        }
        
        if (loadBalanceStrategy == null) {
            loadBalanceStrategy = new RoundRobinLoadBalanceStrategy(namingService);
        }
        lbProxyBean.setLoadBalanceStrategy(loadBalanceStrategy);
        
        if (failOverInterceptor == null) {
            SocketFailOverInterceptor socketFailOverInterceptor = new SocketFailOverInterceptor();
            socketFailOverInterceptor.setRecoverServiceUrls(serverUrls);
            failOverInterceptor = socketFailOverInterceptor;
        }
        lbProxyBean.setFailOverInterceptor(failOverInterceptor);
        lbProxyBean.setTargetBeans(targetBeans);
        lbProxyBean.afterPropertiesSet();
        
        return (T) lbProxyBean.getObject();
    }
    
    public void close() {
        if (lbProxyBean != null) {
            try {
                lbProxyBean.destroy();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

}
