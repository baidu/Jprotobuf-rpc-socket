/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.jprotobuf.pbrpc.client.ha;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.CollectionUtils;

import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.RRNamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.StrategyInterceptor;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;
import com.baidu.jprotobuf.pbrpc.transport.ExceptionHandler;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.utils.ServiceSignatureUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * A enhanced {@link ProtobufRpcProxy} supports naming service and load blanace.
 *
 * @author xiemalin
 * @param <T> the generic type
 * @since 2.16
 */
public class HaProtobufRpcProxy<T> extends NamingServiceChangeListener implements MethodInterceptor {

    /** The Constant LOG. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HaProtobufRpcProxy.class.getName());

    /** The rpc client. */
    private final RpcClient rpcClient;

    /** The interface class. */
    private final Class<T> interfaceClass;

    /** The naming service. */
    private final NamingService namingService;

    /** The load balance strategy factory. */
    private NamingServiceLoadBalanceStrategyFactory loadBalanceStrategyFactory;

    /** The fail over interceptor. */
    private SocketFailOverInterceptor failOverInterceptor;

    /** The proxy instance. */
    private T proxyInstance;

    /** The lookup stub on startup. */
    private boolean lookupStubOnStartup = false;

    /** The instances map. */
    private Map<String, Object> instancesMap = new HashMap<String, Object>();

    /** The lb map. */
    private Map<String, LoadBalanceProxyFactoryBean> lbMap = new HashMap<String, LoadBalanceProxyFactoryBean>();

    /** The protobuf rpc proxy list map. */
    private Map<String, List<ProtobufRpcProxy<T>>> protobufRpcProxyListMap =
            new HashMap<String, List<ProtobufRpcProxy<T>>>();

    /** The proxied. */
    private AtomicBoolean proxied = new AtomicBoolean(false);

    /** The interceptor. */
    protected InvokerInterceptor interceptor;

    /** The exception handler. */
    protected ExceptionHandler exceptionHandler;
    
    /** The strategy interceptor. */
    private StrategyInterceptor strategyInterceptor;
    
    /** The heart beat interval. */
    private long heartBeatInterval = 1000L;
    
    /**
     * Sets the heart beat interval.
     *
     * @param heartBeatInterval the new heart beat interval
     */
    public void setHeartBeatInterval(long heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
    }
    
    
    /**
     * Sets the strategy interceptor.
     *
     * @param strategyInterceptor the new strategy interceptor
     */
    public void setStrategyInterceptor(StrategyInterceptor strategyInterceptor) {
        this.strategyInterceptor = strategyInterceptor;
    }

    /**
     * Sets the exception handler.
     *
     * @param exceptionHandler the new exception handler
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Sets the interceptor.
     *
     * @param interceptor the new interceptor
     */
    public void setInterceptor(InvokerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * Checks if is lookup stub on startup.
     *
     * @return true, if is lookup stub on startup
     */
    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    /**
     * Sets the lookup stub on startup.
     *
     * @param lookupStubOnStartup the new lookup stub on startup
     */
    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    /**
     * Instantiates a new ha protobuf rpc proxy.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
     * @param namingService the naming service
     */
    public HaProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService) {
        this(rpcClient, interfaceClass, namingService, null, null);
    }

    /**
     * Instantiates a new ha protobuf rpc proxy.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
     * @param namingService the naming service
     * @param loadBalanceStrategyFactory the load balance strategy factory
     * @param failOverInterceptor the fail over interceptor
     */
    public HaProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass, NamingService namingService,
            NamingServiceLoadBalanceStrategyFactory loadBalanceStrategyFactory,
            SocketFailOverInterceptor failOverInterceptor) {
        this.rpcClient = rpcClient;
        this.interfaceClass = interfaceClass;
        this.namingService = namingService;
        this.loadBalanceStrategyFactory = loadBalanceStrategyFactory;
        this.failOverInterceptor = failOverInterceptor;
        if (namingService == null) {
            throw new NullPointerException("param 'namingService' is null.");
        }

        ProxyFactory proxyFactory = new ProxyFactory(interfaceClass, this);

        proxyInstance = (T) proxyFactory.getProxy();

    }

    /**
     * On build protobuf rpc proxy.
     *
     * @param rpcClient the rpc client
     * @param interfaceClass the interface class
     * @return the protobuf rpc proxy
     */
    protected ProtobufRpcProxy<T> onBuildProtobufRpcProxy(RpcClient rpcClient, Class<T> interfaceClass) {
        ProtobufRpcProxy<T> protobufRpcProxy = new ProtobufRpcProxy<T>(rpcClient, interfaceClass);
        protobufRpcProxy.setInterceptor(interceptor);
        protobufRpcProxy.setExceptionHandler(exceptionHandler);
        return protobufRpcProxy;
    }

    /**
     * Proxy.
     *
     * @return the t
     * @throws Exception the exception
     */
    public synchronized T proxy() throws Exception {
        if (proxied.compareAndSet(false, true)) {
            ProtobufRpcProxy<T> protobufRpcProxy = onBuildProtobufRpcProxy(rpcClient, interfaceClass);

            // get server list from NamingService
            Map<String, List<RegisterInfo>> servers = namingService.list(protobufRpcProxy.getServiceSignatures());
            // start update naming service task
            startUpdateNamingServiceTask(servers);
            createServiceProxy(servers);
        }

        return proxyInstance;
    }

    /**
     * Creates the service proxy.
     *
     * @param servers the servers
     * @throws Exception the exception
     */
    private void createServiceProxy(Map<String, List<RegisterInfo>> servers) throws Exception {

        Iterator<Entry<String, List<RegisterInfo>>> iter = servers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, List<RegisterInfo>> next = iter.next();
            doProxy(next.getKey(), next.getValue());
        }
    }

    /**
     * Do proxy.
     *
     * @param service the service
     * @param serversList the servers list
     * @throws Exception the exception
     */
    private void doProxy(String service, List<RegisterInfo> serversList) throws Exception {
        long current = System.currentTimeMillis();
        List<RegisterInfo> servers = serversList;
        if (CollectionUtils.isEmpty(servers)) {
            servers = new ArrayList<RegisterInfo>();
        }
        LOGGER.info("Begin: proxy service [" + service + "] for target servicesList of size:" + servers.size());

        LoadBalanceProxyFactoryBean lbProxyBean = new LoadBalanceProxyFactoryBean();
        lbProxyBean.setStrategyInterceptor(strategyInterceptor);
        lbProxyBean.setServiceInterface(interfaceClass);
        lbProxyBean.setRecoverInterval(heartBeatInterval);
        List<ProtobufRpcProxy<T>> protobufRpcProxyList = new ArrayList<ProtobufRpcProxy<T>>();
        Map<String, String> serverUrls = new HashMap<String, String>(servers.size());
        Map<String, Object> targetBeans = new HashMap<String, Object>();
        for (RegisterInfo address : servers) {
            String serviceUrl = address.getHost() + ":" + address.getPort();
            if (serverUrls.containsKey(serviceUrl)) {
                continue;
            }

            serverUrls.put(serviceUrl, serviceUrl);

            ProtobufRpcProxy<T> protobufRpcProxy = onBuildProtobufRpcProxy(rpcClient, interfaceClass);
            protobufRpcProxy.setHost(address.getHost());
            protobufRpcProxy.setPort(address.getPort());
            protobufRpcProxy.setLookupStubOnStartup(lookupStubOnStartup);

            T rpc = protobufRpcProxy.proxy();

            protobufRpcProxyList.add(protobufRpcProxy);

            targetBeans.put(serviceUrl, rpc);

        }

        if (loadBalanceStrategyFactory == null) {
            loadBalanceStrategyFactory = new RRNamingServiceLoadBalanceStrategyFactory();
        }
        lbProxyBean.setLoadBalanceStrategy(loadBalanceStrategyFactory.create(service, namingService));

        if (failOverInterceptor == null) {
            SocketFailOverInterceptor socketFailOverInterceptor = new SocketFailOverInterceptor();
            lbProxyBean.setFailOverInterceptor(socketFailOverInterceptor);
        } else {
            lbProxyBean.setFailOverInterceptor(failOverInterceptor);
        }

        lbProxyBean.setTargetBeans(targetBeans);
        lbProxyBean.afterPropertiesSet();

        protobufRpcProxyListMap.put(service, protobufRpcProxyList);
        lbMap.put(service, lbProxyBean);
        instancesMap.put(service, (T) lbProxyBean.getObject());

        LOGGER.info("Finished:proxy service [" + service + "] for target servicesList of size:" + servers.size()
                + " time took:" + (System.currentTimeMillis() - current) + " ms");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingServiceChangeListener#close()
     */
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
     * do close action.
     *
     * @param lbProxyBean {@link LoadBalanceProxyFactoryBean}
     * @param protobufRpcProxyList list of {@link ProtobufRpcProxy}
     */
    private void doClose(LoadBalanceProxyFactoryBean lbProxyBean, List<ProtobufRpcProxy<T>> protobufRpcProxyList) {
        if (lbProxyBean != null) {
            try {
                lbProxyBean.destroy();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            lbProxyBean = null;
        }
        if (protobufRpcProxyList != null) {
            for (ProtobufRpcProxy<T> proxy : protobufRpcProxyList) {
                try {
                    proxy.close();
                    proxy = null;
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            protobufRpcProxyList.clear();
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
    protected void reInit(final String service, final List<RegisterInfo> list) throws Exception {
        // store old
        LoadBalanceProxyFactoryBean oldLbProxyBean = lbMap.get(service);
        List<ProtobufRpcProxy<T>> oldProtobufRpcProxyList =
                new ArrayList<ProtobufRpcProxy<T>>(protobufRpcProxyListMap.get(service));

        // create a new instance
        doProxy(service, list);

        try {
            // try to close old
            doClose(oldLbProxyBean, oldProtobufRpcProxyList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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
        String methodSignature = ServiceSignatureUtils.makeSignature(serviceName, methodName);
        Object instance = instancesMap.get(methodSignature);
        if (instance == null) {
            throw new NullPointerException("target instance is null may be not initial correct.");
        }
        Object result = invocation.getMethod().invoke(instance, invocation.getArguments());
        return result;
    }

}
