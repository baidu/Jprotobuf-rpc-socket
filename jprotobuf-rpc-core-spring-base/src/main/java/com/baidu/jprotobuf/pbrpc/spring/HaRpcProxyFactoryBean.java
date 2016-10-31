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
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;

/**
 * {@link FactoryBean} for Ha PbRpc proxies.
 *
 * @author xiemalin
 * @since 2.17
 */
public class HaRpcProxyFactoryBean extends RpcClientOptions
        implements FactoryBean, InitializingBean, MethodInterceptor, DisposableBean {

    /** The service interface. */
    private Class<Object> serviceInterface;

    /** The pbrpc proxy. */
    private HaProtobufRpcProxyBean<Object> pbrpcProxy;
    
    /** The service proxy. */
    private Object serviceProxy;
    
    /** The rpc client. */
    private RpcClient rpcClient;

    /** The naming service. */
    private NamingService namingService;

    /** The naming service load balance strategy factory. */
    private NamingServiceLoadBalanceStrategyFactory namingServiceLoadBalanceStrategyFactory;
    
    /** The fail over interceptor. */
    private SocketFailOverInterceptor failOverInterceptor;
    
	/** The interceptor. */
	private InvokerInterceptor interceptor;

	/**
	 * Sets the interceptor.
	 *
	 * @param interceptor the new interceptor
	 */
	public void setInterceptor(InvokerInterceptor interceptor) {
		this.interceptor = interceptor;
	}

    /** The lookup stub on startup. */
    private boolean lookupStubOnStartup = true;

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
     * Sets the naming service.
     *
     * @param namingService the new naming service
     */
    public void setNamingService(NamingService namingService) {
        this.namingService = namingService;
    }

    /**
     * Sets the service interface.
     *
     * @param serviceInterface the new service interface
     */
    public void setServiceInterface(Class serviceInterface) {
        if (serviceInterface != null && !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }

    /**
     * Gets the service interface.
     *
     * @return the service interface
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
        pbrpcProxy.setLookupStubOnStartup(lookupStubOnStartup);
        pbrpcProxy.setInterceptor(interceptor);
        pbrpcProxy.proxy();

        ProxyFactory proxyFactory = new ProxyFactory(getServiceInterface(), this);
        this.serviceProxy = proxyFactory.getProxy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return pbrpcProxy.invoke(invocation);
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
     * Sets the naming service load balance strategy factory.
     *
     * @param namingServiceLoadBalanceStrategyFactory the new naming service load balance strategy factory
     */
    public void setNamingServiceLoadBalanceStrategyFactory(
            NamingServiceLoadBalanceStrategyFactory namingServiceLoadBalanceStrategyFactory) {
        this.namingServiceLoadBalanceStrategyFactory = namingServiceLoadBalanceStrategyFactory;
    }
    
    /**
     * Sets the fail over interceptor.
     *
     * @param failOverInterceptor the new fail over interceptor
     */
    public void setFailOverInterceptor(SocketFailOverInterceptor failOverInterceptor) {
        this.failOverInterceptor = failOverInterceptor;
    }

}
