/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import junit.framework.Assert;

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;

/**
 * 
 * Test for {@link EchoService}
 *
 * @author xiemalin
 * @since 1.0
 * @see EchoService
 * 
 */
public class EchoServiceTest extends BaseEchoServiceTest {
    
    /**
     * test client auto recover connection from server.
     */
    @Test
    public void testAutoRecoverConnection() {

        EchoInfo echoInfo = getEchoInfo();
        
        EchoServiceImpl ecohImpl = new EchoServiceImpl();
        
        EchoInfo response = echoService.echo(echoInfo);
        Assert.assertEquals(ecohImpl.doEcho(echoInfo).getMessage(), response.getMessage());
        
        // here stop server
        stopServer();
        
        try {
            echoService.echo(echoInfo);
            Assert.fail("should be connect server failed.");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        
        startServer();
        
        response = echoService.echo(echoInfo);
        Assert.assertEquals(ecohImpl.doEcho(echoInfo).getMessage(), response.getMessage());
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
    
    @Test
    public void testAttachment() {
        EchoInfo echoInfo = getEchoInfo();
        
        EchoServiceImpl ecohImpl = new EchoServiceImpl();
        
        EchoInfo response = echoService.echoWithAttachement(echoInfo);
        Assert.assertEquals(ecohImpl.doEcho(echoInfo).getMessage(), response.getMessage());
    }
    

    /**
     * 
     */
    @Test
    public void testEchoServiceClient() {
        
        
        RpcServer rpcServer = new RpcServer();
        
        EchoServiceImpl echoServiceImpl = new EchoServiceImpl();
        rpcServer.registerService(echoServiceImpl);
        rpcServer.start(PORT);
        
        
        RpcClientOptions options = new RpcClientOptions();
        options.setMaxWait(1);
        RpcClient rpcClient = new RpcClient(options);
        
        
        ProtobufRpcProxy<EchoService> pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
        pbrpcProxy.setPort(PORT);
        EchoService echoService = pbrpcProxy.proxy();
        
        try {
            long time = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                EchoInfo echo = echoService.echo(new EchoInfo("hello"));
                System.out.println("time took:" + (System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                echo = echoService.echo(new EchoInfo("hello"));
            }
            System.out.println("time took:" + (System.currentTimeMillis() - time));
/*            rpcServer.shutdown();
            
            rpcServer = new RpcServer();
            rpcServer.registerService(echoServiceImpl);
            rpcServer.start(PORT);
            echo = echoService.echo(new EchoInfo("hello"));*/
            
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
