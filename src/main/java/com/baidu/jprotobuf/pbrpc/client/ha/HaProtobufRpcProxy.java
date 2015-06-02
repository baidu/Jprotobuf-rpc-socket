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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.CollectionUtils;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.RoundRobinLoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * A enhanced {@link ProtobufRpcProxy} supports naming service and load blanace.
 * 
 * 
 * @author xiemalin
 * @since 2.16
 */
public class HaProtobufRpcProxy<T> extends NamingServiceChangeListener implements MethodInterceptor {

    private final RpcClient rpcClient;
    private final Class<T> interfaceClass;
    private final NamingService namingService;
    private NamingServiceLoadBalanceStrategy loadBalanceStrategy;
    private FailOverInterceptor failOverInterceptor;
    private LoadBalanceProxyFactoryBean lbProxyBean;
    private T proxyInstance;

    private T instance;
    private List<ProtobufRpcProxy<T>> protobufRpcProxyList = new ArrayList<ProtobufRpcProxy<T>>();

    private boolean lookupStubOnStartup = true;

    /**
     * get the lookupStubOnStartup
     * 
     * @return the lookupStubOnStartup
     */
    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    /**
     * set lookupStubOnStartup value to lookupStubOnStartup
     * 
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
            NamingServiceLoadBalanceStrategy loadBalanceStrategy, FailOverInterceptor failOverInterceptor) {
        this.rpcClient = rpcClient;
        this.interfaceClass = interfaceClass;
        this.namingService = namingService;
        this.loadBalanceStrategy = loadBalanceStrategy;
        this.failOverInterceptor = failOverInterceptor;
        if (namingService == null) {
            throw new NullPointerException("param 'namingService' is null.");
        }
        proxyInstance = (T) new ProxyFactory(interfaceClass, this).getProxy();

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
        // start update naming service task
        startUpdateNamingServiceTask(servers);

        instance = doProxy(servers);
        return proxyInstance;
    }
    
    /**
     * @param servers
     * @return
     * @throws Exception
     */
    private T doProxy(List<InetSocketAddress> serversList) throws Exception {
        List<InetSocketAddress> servers = serversList;
        if (CollectionUtils.isEmpty(servers)) {
            servers = new ArrayList<InetSocketAddress>();
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

        return (T) lbProxyBean.getObject();
    }

    public void close() {
        doClose(lbProxyBean, protobufRpcProxyList);
        super.close();
    }

    /**
     * do close action
     * 
     * @param lbProxyBean {@link LoadBalanceProxyFactoryBean}
     * @param protobufRpcProxyList list of {@link ProtobufRpcProxy}
     */
    private void doClose(LoadBalanceProxyFactoryBean lbProxyBean, List<ProtobufRpcProxy<T>> protobufRpcProxyList) {
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

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingServiceChangeListener#getNamingService()
     */
    @Override
    public NamingService getNamingService() {
        return namingService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingServiceChangeListener#reInit(java.util.List)
     */
    @Override
    protected void reInit(final List<InetSocketAddress> list) throws Exception {
        // store old
        LoadBalanceProxyFactoryBean oldLbProxyBean = lbProxyBean;
        List<ProtobufRpcProxy<T>> oldProtobufRpcProxyList = new ArrayList<ProtobufRpcProxy<T>>(protobufRpcProxyList);
        protobufRpcProxyList.clear();

        // reinit naming service
        loadBalanceStrategy.doReInit(new NamingService() {
            @Override
            public List<InetSocketAddress> list() throws Exception {
                return list;
            }
        });
        
        
        // create a new instance
        T instance = doProxy(list);
        this.instance = instance;
        try {
            // try to close old
            doClose(oldLbProxyBean, oldProtobufRpcProxyList);
        } catch (Exception e) {
            LOGGER.fatal(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (instance == null) {
            throw new NullPointerException("target instance is null may be not initial correct.");
        }
        Object result = invocation.getMethod().invoke(instance, invocation.getArguments());
        return result;
    }

}
