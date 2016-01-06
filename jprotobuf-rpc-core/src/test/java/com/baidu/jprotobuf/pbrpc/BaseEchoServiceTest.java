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

package com.baidu.jprotobuf.pbrpc;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;

/**
 * Base test class for echo RPC server and client
 *
 * @author xiemalin
 * @since 1.0
 */
public abstract class BaseEchoServiceTest extends BaseTest {
    
    private static final Logger LOG = Logger.getLogger(BaseEchoServiceTest.class.getName());

    protected RpcServer rpcServer;
    protected EchoService echoService;
    private RpcClient rpcClient;
    private ProtobufRpcProxy<EchoService> pbrpcProxy;
    
    @Before
    public void setUp() {
        RpcServerOptions rpcServerOptions = getRpcServerOptions();
        if (rpcServerOptions == null) {
            rpcServerOptions = new RpcServerOptions();
        }
        rpcServer = new RpcServer(rpcServerOptions);
        rpcServer.setInterceptor(new InvokerInterceptor() {
			
			@Override
			public Object process(Object target, Method method, Object[] args) {
				System.out.println("server intercepr method:" + method);
				return null;
			}
			
			@Override
			public void beforeInvoke(Object target, Method method, Object[] args) {
				Assert.assertNotNull(method);
			}
		});
        
        EchoServiceImpl echoServiceImpl = new EchoServiceImpl();
        rpcServer.registerService(echoServiceImpl);
        rpcServer.start(PORT);
        
        RpcClientOptions options = getRpcClientOptions();
        if (options == null) {
            options = new RpcClientOptions();
        }
        rpcClient = new RpcClient(options);
        
        pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
        pbrpcProxy.setPort(PORT);
        
        pbrpcProxy.setInterceptor(new InvokerInterceptor() {
			
			@Override
			public Object process(Object target, Method method, Object[] args) {
				System.out.println("client intercepr method:" + method);
				return null;
			}
			
			@Override
			public void beforeInvoke(Object target, Method method, Object[] args) {
				Assert.assertNotNull(method);
			}
		});
        
        echoService = pbrpcProxy.proxy();
    }
    
    protected void startServer() {
        RpcServerOptions rpcServerOptions = getRpcServerOptions();
        if (rpcServerOptions == null) {
            rpcServerOptions = new RpcServerOptions();
        }
        rpcServer = new RpcServer(rpcServerOptions);
        
        EchoServiceImpl echoServiceImpl = new EchoServiceImpl();
        rpcServer.registerService(echoServiceImpl);
        rpcServer.start(PORT);
    }
    
    protected void stopServer() {
        try {
            rpcServer.shutdown();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    protected RpcClientOptions getRpcClientOptions() {
        return null;
    }
    
    protected RpcServerOptions getRpcServerOptions() {
        return null;
    }
    
    
    @After
    public void tearDown() {
        try {
            rpcClient.stop();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        if (pbrpcProxy !=  null) {
            pbrpcProxy.close();
        }
        stopServer();
    }
    
}
