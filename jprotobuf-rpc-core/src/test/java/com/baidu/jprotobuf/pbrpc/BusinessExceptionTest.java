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

import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;

/**
 * Test case for business exception.
 *
 * @author xiemalin
 * @since 2.23
 */

public class BusinessExceptionTest extends BaseTest {
    
    private static final Logger LOG = Logger.getLogger(BusinessExceptionTest.class.getName());

    
    private RpcServer rpcServer;
    private RpcClient rpcClient;
    private ProtobufRpcProxy<EchoService> pbrpcProxy;
    private EchoService echoService;

    @Before
    public void setUp() {
        RpcServerOptions rpcServerOptions = getRpcServerOptions();
        if (rpcServerOptions == null) {
            rpcServerOptions = new RpcServerOptions();
        }
        rpcServer = new RpcServer(rpcServerOptions);
        
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
        echoService = pbrpcProxy.proxy();
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
    
    /**
     * @return
     */
    private RpcClientOptions getRpcClientOptions() {
        return null;
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
    
    /**
     * @return
     */
    private RpcServerOptions getRpcServerOptions() {
        return null;
    }

    protected void stopServer() {
        try {
            rpcServer.shutdown();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    /**
     * @return
     */
    private EchoInfo getEchoInfo() {
        String message = "xiemalin";
        // test success
        EchoInfo echoInfo = new EchoInfo();
        echoInfo.setMessage(message);
        return echoInfo;
    }
    
    /**
     * test for business exception case
     */
    @Test
    public void testBusinessException() {
        try {
            echoService.businessExceptionCall(getEchoInfo());
            Assert.fail("should throw exception.");
        } catch (UndeclaredThrowableException e) {
            Assert.assertTrue(e.getUndeclaredThrowable().getMessage().length() > 0);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().length() > 0);
        }
        
    }
}
