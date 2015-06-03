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

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.CollectionUtils;

import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.RoundRobinLoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

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
    private T proxyInstance;

    private boolean lookupStubOnStartup = true;

    private Map<String, Object> instancesMap = new HashMap<String, Object>();
    private Map<String, LoadBalanceProxyFactoryBean> lbMap = new HashMap<String, LoadBalanceProxyFactoryBean>();
    private Map<String, List<ProtobufRpcProxy<T>>> protobufRpcProxyListMap =
            new HashMap<String, List<ProtobufRpcProxy<T>>>();

    private AtomicBoolean proxied = new AtomicBoolean(false);

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
        if (proxied.compareAndSet(false, true)) {
            ProtobufRpcProxy<T> protobufRpcProxy = onBuildProtobufRpcProxy(rpcClient, interfaceClass);

            // get server list from NamingService
            Map<String, List<InetSocketAddress>> servers = namingService.list(protobufRpcProxy.getServiceSignatures());
            // start update naming service task
            startUpdateNamingServiceTask(servers);

            createServiceProxy(servers);
        }

        return proxyInstance;
    }

    private void createServiceProxy(Map<String, List<InetSocketAddress>> servers) throws Exception {

        Iterator<Entry<String, List<InetSocketAddress>>> iter = servers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, List<InetSocketAddress>> next = iter.next();
            doProxy(next.getKey(), next.getValue());
        }
    }

    /**
     * @param servers
     * @return
     * @throws Exception
     */
    private void doProxy(String service, List<InetSocketAddress> serversList) throws Exception {
        List<InetSocketAddress> servers = serversList;
        if (CollectionUtils.isEmpty(servers)) {
            servers = new ArrayList<InetSocketAddress>();
        }

        LoadBalanceProxyFactoryBean lbProxyBean = new LoadBalanceProxyFactoryBean();
        lbProxyBean.setServiceInterface(interfaceClass);
        List<ProtobufRpcProxy<T>> protobufRpcProxyList = new ArrayList<ProtobufRpcProxy<T>>();
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
            loadBalanceStrategy = new RoundRobinLoadBalanceStrategy(service, namingService);
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

        protobufRpcProxyListMap.put(service, protobufRpcProxyList);
        lbMap.put(service, lbProxyBean);
        instancesMap.put(service, (T) lbProxyBean.getObject());
    }

    public void close() {
        Collection<List<ProtobufRpcProxy<T>>> values = protobufRpcProxyListMap.values();
        for (List<ProtobufRpcProxy<T>> list : values) {
            doClose(null, list);
        }

        Collection<LoadBalanceProxyFactoryBean> lbs = lbMap.values();
        for (LoadBalanceProxyFactoryBean loadBalanceProxyFactoryBean : lbs) {
            doClose(loadBalanceProxyFactoryBean, null);
        }
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
    protected void reInit(final String service, final List<InetSocketAddress> list) throws Exception {
        // store old
        LoadBalanceProxyFactoryBean oldLbProxyBean = lbMap.get(service);
        List<ProtobufRpcProxy<T>> oldProtobufRpcProxyList =
                new ArrayList<ProtobufRpcProxy<T>>(protobufRpcProxyListMap.get(service));

        // reinit naming service
        loadBalanceStrategy.doReInit(service, new NamingService() {

            @Override
            public Map<String, List<InetSocketAddress>> list(Set<String> serviceSignatures) throws Exception {
                Map<String, List<InetSocketAddress>> ret = new HashMap<String, List<InetSocketAddress>>();
                ret.put(service, list);
                return ret;
            }

        });

        // create a new instance
        doProxy(service, list);
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
        Method method = invocation.getMethod();
        ProtobufRPC protobufPRC = method.getAnnotation(ProtobufRPC.class);
        if (protobufPRC == null) {
            throw new IllegalAccessError("Target method is not marked annotation @ProtobufPRC. method name :"
                    + method.getDeclaringClass().getName() + "." + method.getName());
        }
        String serviceName = protobufPRC.serviceName();
        String methodName = protobufPRC.methodName();
        if (StringUtils.isEmpty(methodName)) {
            methodName = method.getName();
        }
        String methodSignature = serviceName + '!' + methodName;
        Object instance = instancesMap.get(methodSignature);
        if (instance == null) {
            throw new NullPointerException("target instance is null may be not initial correct.");
        }
        Object result = invocation.getMethod().invoke(instance, invocation.getArguments());
        return result;
    }

}
