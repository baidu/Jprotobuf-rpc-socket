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
