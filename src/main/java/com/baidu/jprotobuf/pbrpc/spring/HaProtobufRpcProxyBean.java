/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationFactory;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.HaProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * To enhance {@link HaProtobufRpcProxy} to process extra parameters transportation.
 * 
 * @author xiemalin
 * @since 2.17
 */
public class HaProtobufRpcProxyBean<T> extends HaProtobufRpcProxy<T> implements MethodInterceptor {
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
    
    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.HaProtobufRpcProxy#onBuildProtobufRpcProxy(com.baidu.jprotobuf.pbrpc.transport.RpcClient, java.lang.Class)
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
            
            Object result = invocation.getMethod().invoke(proxyBean, invocation.getArguments());
            return result;
        } finally {
            CURRENT_PARAMS.remove();
        }
    }
}
