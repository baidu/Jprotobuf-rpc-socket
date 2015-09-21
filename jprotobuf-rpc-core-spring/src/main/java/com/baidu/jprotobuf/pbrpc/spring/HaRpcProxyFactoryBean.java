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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.util.Assert;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;

/**
 * 
 * {@link FactoryBean} for Ha PbRpc proxies
 * 
 * @author xiemalin
 * @since 2.17
 */
public class HaRpcProxyFactoryBean extends RpcClientOptions
        implements FactoryBean<Object>, InitializingBean, MethodInterceptor, DisposableBean {

    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

    private Class<Object> serviceInterface;

    private HaProtobufRpcProxyBean<Object> pbrpcProxy;
    private Object serviceProxy;
    private RpcClient rpcClient;

    private NamingService namingService;

    private NamingServiceLoadBalanceStrategyFactory namingServiceLoadBalanceStrategyFactory;
    private SocketFailOverInterceptor failOverInterceptor;

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
     * set namingService value to namingService
     * 
     * @param namingService the namingService to set
     */
    public void setNamingService(NamingService namingService) {
        this.namingService = namingService;
    }

    /**
     * Set the interface of the service to access. The interface must be suitable for the particular service and
     * remoting strategy.
     * <p>
     * Typically required to be able to create a suitable service proxy, but can also be optional if the lookup returns
     * a typed proxy.
     */
    public void setServiceInterface(Class serviceInterface) {
        if (serviceInterface != null && !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }

    /**
     * Return the interface of the service to access.
     */
    public Class getServiceInterface() {
        return this.serviceInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {
        return serviceProxy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return serviceInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(namingService, "property 'namingService' is null.");

        rpcClient = new RpcClient(this);
        // 创建EchoService代理
        pbrpcProxy = new HaProtobufRpcProxyBean<Object>(rpcClient, serviceInterface, namingService,
                namingServiceLoadBalanceStrategyFactory, failOverInterceptor);
        pbrpcProxy.setRemoteInvocationFactory(remoteInvocationFactory);
        pbrpcProxy.setLookupStubOnStartup(lookupStubOnStartup);
        pbrpcProxy.proxy();

        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return pbrpcProxy.invoke(invocation);
    }

    /**
     * set remoteInvocationFactory value to remoteInvocationFactory
     * 
     * @param remoteInvocationFactory the remoteInvocationFactory to set
     */
    protected void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = remoteInvocationFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        if (pbrpcProxy != null) {
            pbrpcProxy.close();
        }
        if (rpcClient != null) {
            rpcClient.stop();
        }
    }
    
    /**
     * set namingServiceLoadBalanceStrategyFactory value to namingServiceLoadBalanceStrategyFactory
     * @param namingServiceLoadBalanceStrategyFactory the namingServiceLoadBalanceStrategyFactory to set
     */
    public void setNamingServiceLoadBalanceStrategyFactory(
            NamingServiceLoadBalanceStrategyFactory namingServiceLoadBalanceStrategyFactory) {
        this.namingServiceLoadBalanceStrategyFactory = namingServiceLoadBalanceStrategyFactory;
    }
    
    /**
     * set failOverInterceptor value to failOverInterceptor
     * @param failOverInterceptor the failOverInterceptor to set
     */
    public void setFailOverInterceptor(SocketFailOverInterceptor failOverInterceptor) {
        this.failOverInterceptor = failOverInterceptor;
    }

}
