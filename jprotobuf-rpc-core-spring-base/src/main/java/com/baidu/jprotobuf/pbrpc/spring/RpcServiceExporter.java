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

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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

    /** The pr rpc server. */
    private RpcServer prRpcServer;
    
    /** The service port. */
    private int servicePort;
    
    /** The host. */
    private String host;
    
    /** The register services. */
    private List<Object> registerServices;
    
    /** The rpc service registry bean. */
    private RpcServiceRegistryBean rpcServiceRegistryBean;
    
    /** The registry center service. */
    private RegistryCenterService registryCenterService;
    
    /** The cached rister info list. */
    private List<RegisterInfo> cachedRisterInfoList;
    
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
    

    /**
     * Sets the registry center service.
     *
     * @param registryCenterService the new registry center service
     */
    public void setRegistryCenterService(RegistryCenterService registryCenterService) {
        this.registryCenterService = registryCenterService;
    }


    /**
     * Sets the rpc service registry bean.
     *
     * @param rpcServiceRegistryBean the new rpc service registry bean
     */
    public void setRpcServiceRegistryBean(RpcServiceRegistryBean rpcServiceRegistryBean) {
        this.rpcServiceRegistryBean = rpcServiceRegistryBean;
    }

    /**
     * Gets the register services.
     *
     * @return the register services
     */
    public List<Object> getRegisterServices() {
        return registerServices;
    }

    /**
     * Sets the register services.
     *
     * @param registerServices the new register services
     */
    public void setRegisterServices(List<Object> registerServices) {
        this.registerServices = registerServices;
    }

    /**
     * Gets the service port.
     *
     * @return the service port
     */
    protected int getServicePort() {
        return servicePort;
    }

    /**
     * Sets the service port.
     *
     * @param servicePort the new service port
     */
    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    protected String getHost() {
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
