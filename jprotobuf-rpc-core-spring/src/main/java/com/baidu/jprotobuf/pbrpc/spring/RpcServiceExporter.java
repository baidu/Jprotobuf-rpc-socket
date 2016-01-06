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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;
import com.baidu.jprotobuf.pbrpc.registry.RegistryCenterService;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;
import com.baidu.jprotobuf.pbrpc.utils.Constants;
import com.baidu.jprotobuf.pbrpc.utils.NetUtils;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * PBRPC exporter for standard PROTOBUF RPC implementation from jprotobuf-rpc-socket.
 * 
 * @author xiemalin
 * @since 2.17
 */
public class RpcServiceExporter extends RpcServerOptions implements InitializingBean, DisposableBean {

    private RpcServer prRpcServer;
    
    private int servicePort;
    
    private String host;
    
    private List<Object> registerServices;
    
    private RpcServiceRegistryBean rpcServiceRegistryBean;
    
    private RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();
    
    private RegistryCenterService registryCenterService;
    
    private List<RegisterInfo> cachedRisterInfoList;
    
	private InvokerInterceptor interceptor;

	/**
	 * set interceptor value to interceptor
	 * 
	 * @param interceptor
	 *            the interceptor to set
	 */
	public void setInterceptor(InvokerInterceptor interceptor) {
		this.interceptor = interceptor;
	}
    

    /**
     * set registryCenterService value to registryCenterService
     * @param registryCenterService the registryCenterService to set
     */
    public void setRegistryCenterService(RegistryCenterService registryCenterService) {
        this.registryCenterService = registryCenterService;
    }

    /**
     * Set the RemoteInvocationExecutor to use for this exporter.
     * Default is a DefaultRemoteInvocationExecutor.
     * <p>A custom invocation executor can extract further context information
     * from the invocation, for example user credentials.
     */
    public void setRemoteInvocationExecutor(RemoteInvocationExecutor remoteInvocationExecutor) {
        this.remoteInvocationExecutor = remoteInvocationExecutor;
    }

    /**
     * Return the RemoteInvocationExecutor used by this exporter.
     */
    public RemoteInvocationExecutor getRemoteInvocationExecutor() {
        return this.remoteInvocationExecutor;
    }
    
    /**
     * set rpcServiceRegistryBean value to rpcServiceRegistryBean
     * @param rpcServiceRegistryBean the rpcServiceRegistryBean to set
     */
    public void setRpcServiceRegistryBean(RpcServiceRegistryBean rpcServiceRegistryBean) {
        this.rpcServiceRegistryBean = rpcServiceRegistryBean;
    }

    /**
     * get the registerServices
     * @return the registerServices
     */
    public List<Object> getRegisterServices() {
        return registerServices;
    }

    /**
     * set registerServices value to registerServices
     * @param registerServices the registerServices to set
     */
    public void setRegisterServices(List<Object> registerServices) {
        this.registerServices = registerServices;
    }

    /**
     * get the servicePort
     * @return the servicePort
     */
    protected int getServicePort() {
        return servicePort;
    }

    /**
     * set servicePort value to servicePort
     * @param servicePort the servicePort to set
     */
    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    /**
     * get the host
     * @return the host
     */
    protected String getHost() {
        return host;
    }

    /**
     * set host value to host
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (prRpcServer != null) {
            prRpcServer.shutdown();
        }
        
        if (registryCenterService != null && cachedRisterInfoList != null) {
            for (RegisterInfo registerInfo : cachedRisterInfoList) {
                registryCenterService.unregister(registerInfo);
            }
        }
        
        if (rpcServiceRegistryBean != null) {
            rpcServiceRegistryBean.unRegisterAll();
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(servicePort > 0, "invalid service port: " + servicePort);
        Assert.isTrue(!CollectionUtils.isEmpty(registerServices), "No register service specified.");
        
        if (rpcServiceRegistryBean == null) {
            rpcServiceRegistryBean = new RpcServiceRegistryBean();
        }
        
        rpcServiceRegistryBean.setRemoteInvocationExecutor(remoteInvocationExecutor);
        prRpcServer = new RpcServer(this, rpcServiceRegistryBean);
        prRpcServer.setInterceptor(interceptor);
        
        for (Object service : registerServices) {
            prRpcServer.registerService(service);
        }
        
        String registerHost;
        if (StringUtils.isBlank(host)) {
            prRpcServer.start(new InetSocketAddress(servicePort));
            registerHost = NetUtils.getLocalAddress().getHostAddress();
        } else {
            prRpcServer.start(new InetSocketAddress(host, servicePort));
            registerHost = host;
        }
        
        if (registryCenterService != null) {
            cachedRisterInfoList = new ArrayList<RegisterInfo>();
            Collection<RpcHandler> services = rpcServiceRegistryBean.getServices();
            List<RpcHandler> list = new ArrayList<RpcHandler>(services);
            for (RpcHandler rpcHandler : list) {
                RegisterInfo registerInfo = new RegisterInfo();
                registerInfo.setHost(registerHost);
                registerInfo.setPort(servicePort);
                registerInfo.setProtocol(Constants.PBRPC_SCHEME);
                registerInfo.setService(rpcHandler.getMethodSignature());
                registryCenterService.register(registerInfo);
                
                cachedRisterInfoList.add(registerInfo);
            }
        }
    }

}
