/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import java.lang.reflect.InvocationTargetException;

import org.springframework.remoting.support.RemoteInvocation;

import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.server.RpcData;


/**
 * to override invoke method
 * 
 * @author xiemalin
 * @since 2.17
 */
public class RemoteInvocationProxy extends RemoteInvocation {

    private RpcHandler rpcHandler;
    
    private RpcData rpcData;
    
    /**
     * @param rpcHandler
     * @param rpcData
     */
    public RemoteInvocationProxy(RpcHandler rpcHandler, RpcData rpcData) {
        super();
        this.rpcHandler = rpcHandler;
        this.rpcData = rpcData;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.remoting.support.RemoteInvocation#invoke(java.lang
     * .Object)
     */
    @Override
    public Object invoke(Object param) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        try {
            return rpcHandler.doHandle(rpcData);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
