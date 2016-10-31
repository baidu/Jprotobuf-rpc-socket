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

import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;

/**
 * {@link FactoryBean} for PbRpc proxies.
 *
 * @author xiemalin
 * @since 2.17
 */
public class RpcProxyFactoryBean extends RpcClientOptions 
        implements FactoryBean, InitializingBean, MethodInterceptor, DisposableBean {
    
    /** The service interface. */
    private Class serviceInterface;

    /** The host. */
    private String host;

    /** The port. */
    private int port; 
    
    /** The pbrpc proxy. */
    private ProtobufRpcProxyBean pbrpcProxy;
    
    /** The service proxy. */
    private Object serviceProxy;
    
    /** The rpc client. */
    private RpcClient rpcClient;
    
    /** The lookup stub on startup. */
    private boolean lookupStubOnStartup = true;
    
	/** The interceptor. */
	private InvokerInterceptor interceptor;

	/** The proxy. */
	private Object proxy;
	
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
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host.
     *
     * @param host the new host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
        this.port = port;
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
    @Override
    public Object getObject() throws Exception {
        return serviceProxy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class getObjectType() {
        return serviceInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(port > 0, "invalid service port: " + port);
        
        rpcClient = new RpcClient(this);
        // 创建EchoService代理
        pbrpcProxy = new ProtobufRpcProxyBean(rpcClient, serviceInterface);
        pbrpcProxy.setPort(port);
        pbrpcProxy.setHost(host);
        pbrpcProxy.setLookupStubOnStartup(lookupStubOnStartup);
        
        pbrpcProxy.setInterceptor(interceptor);
        
        // 动态生成代理实
        proxy = pbrpcProxy.proxy();
        pbrpcProxy.setProxyBean(proxy);
        
        ProxyFactory proxyFactory = new ProxyFactory(getServiceInterface(), this);
 
        
        this.serviceProxy = proxyFactory.getProxy();
    }
    
    /**
     * Gets the proxy bean.
     *
     * @return the proxy bean
     */
	public Object getProxyBean() {
		return proxy;
	}

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return pbrpcProxy.invoke(invocation);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (pbrpcProxy != null) {
            pbrpcProxy.close();
        }
        if (rpcClient != null) {
            rpcClient.stop();
        }
    }


}
