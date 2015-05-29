/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import java.lang.reflect.Method;

import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationExecutor;

import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.server.AbstractRpcHandler;
import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;

/**
 * Supports {@link RemoteInvocationExecutor} parse
 * 
 * @author xiemalin
 * @since 2.17
 */
public class RpcServiceRegistryBean extends RpcServiceRegistry {

    private RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();
    
    public RpcServiceRegistryBean() {
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
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry#doCreateRpcHandler
     * (java.lang.reflect.Method, java.lang.Object,
     * com.baidu.jprotobuf.pbrpc.ProtobufPRCService)
     */
    @Override
    protected RpcHandler doCreateRpcHandler(Method method, 
            Object service, ProtobufRPCService protobufPRCService) {
        
        RpcHandler handler = super.doCreateRpcHandler(method, service, protobufPRCService);
        RemoteExcuteInvokeRpcHandler wrap = new RemoteExcuteInvokeRpcHandler((AbstractRpcHandler) handler);
        wrap.setRemoteInvocationExecutor(remoteInvocationExecutor);
        return wrap;
    }
}
