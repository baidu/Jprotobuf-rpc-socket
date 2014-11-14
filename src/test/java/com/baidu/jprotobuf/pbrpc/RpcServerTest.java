/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

import java.util.Arrays;

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcMeta;
import com.baidu.jprotobuf.pbrpc.transport.BlockingRpcCallback;
import com.baidu.jprotobuf.pbrpc.transport.RpcChannel;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;

/**
 * Test class for RpcServer
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServerTest {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8031"));

    /**
     * test server start
     * 
     * @throws InterruptedException
     */
    @Test
    public void testServerStart() throws InterruptedException {
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(PORT);
        
        
        RpcClient rpcClient = new RpcClient();
        
        //build package
        
        RpcDataPackage dataPacage = new RpcDataPackage();
        dataPacage.magicCode(ProtocolConstant.MAGIC_CODE);
        dataPacage.serviceName("sn").methodName("method").data(new byte[] {1, 2, 4, 8});
        dataPacage.logId(1L).correlationId(2L);
        dataPacage.compressType(RpcMeta.COMPRESS_NO);
        
        RpcChannel rpcChannel = new RpcChannel(rpcClient, HOST, PORT);
        
        BlockingRpcCallback callback = new BlockingRpcCallback();
        rpcChannel.doTransport(dataPacage, callback, 100000);
        
        if (!callback.isDone()) {
            synchronized (callback) {
                while (!callback.isDone()) {
                    try {
                        callback.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        if (callback.getMessage() != null) {
            System.out.println(Arrays.toString(callback.getMessage().getData()));
        }
        
    }
}
