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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private T instance;
    private List<ProtobufRpcProxy<T>> protobufRpcProxyList = new ArrayList<ProtobufRpcProxy<T>>();
    
    private boolean lookupStubOnStartup = true;
    
    /**
     * get the lookupStubOnStartup
     * @return the lookupStubOnStartup
     */
    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    /**
     * set lookupStubOnStartup value to lookupStubOnStartup
     * @param lookupStubOnStartup the lookupStubOnStartup to set
     */
    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    /**
     * log this class
     */
    protected static final Log LOGGER = LogFactory.getLog(HaProtobufRpcProxy.class);

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
    
    protected ProtobufRpcProxy<T> onBuildProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass) {
        ProtobufRpcProxy<T> protobufRpcProxy = new ProtobufRpcProxy<T>(rpcClient, interfaceClass);
        return protobufRpcProxy;
    }

    public synchronized T proxy() throws Exception {

        if (instance != null) {
            return instance;
        }

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

            ProtobufRpcProxy<T> protobufRpcProxy = onBuildProtobufRpcProxy(rpcClient, interfaceClass);
            protobufRpcProxy.setHost(address.getHostName());
            protobufRpcProxy.setPort(address.getPort());
            protobufRpcProxy.setLookupStubOnStartup(lookupStubOnStartup);

            T rpc = protobufRpcProxy.proxy();

            protobufRpcProxyList.add(protobufRpcProxy);

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

        instance = (T) lbProxyBean.getObject();
        return instance;
    }

    public void close() {
        if (lbProxyBean != null) {
            try {
                lbProxyBean.destroy();
            } catch (Exception e) {
                LOGGER.fatal(e.getMessage(), e);
            }
        }
        if (protobufRpcProxyList != null) {
            for (ProtobufRpcProxy<T> proxy : protobufRpcProxyList) {
                try {
                    proxy.close();
                } catch (Exception e) {
                    LOGGER.fatal(e.getMessage(), e);
                }
            }
        }
    }

}
