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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;

/**
 * The Class ClientMain.
 *
 * @author xiemalin
 */
public class ClientMain {
    
    
    private EchoService echoService;
    private RpcClient rpcClient;
    private ProtobufRpcProxy<EchoService> pbrpcProxy;

    /**
     * Test client request.
     */
    @Test
    public void testClientRequest() {
        doClientRquest(1);
        
    }
    
    @Before
    public void warmUp() {
        RpcClientOptions options = new RpcClientOptions();
        options.setThreadPoolSize(10);
        options.setMaxIdleSize(10);
        options.setMinIdleSize(10);
        options.setMaxWait(5000);
        options.setShortConnection(false);
        options.setIncludeRemoteServerInfoOnError(true);

        rpcClient = new RpcClient(options);
        pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
        pbrpcProxy.setPort(8000);
        pbrpcProxy.setHost("localhost");
        echoService = pbrpcProxy.proxy();
        EchoInfo echoInfo = new EchoInfo();
        echoInfo.setMessage("warm message");
        echoService.echo(echoInfo);
    }
    
    @After
    public void tearDown() {
        pbrpcProxy.close();
        rpcClient.shutdown();
    }

    /**
     * Do client rquest.
     *
     * @param times the times
     */
    private void doClientRquest(int times) {
        EchoInfo echoInfo = new EchoInfo();
        long time = System.currentTimeMillis();
        List<Future<EchoInfo>> list = new ArrayList<Future<EchoInfo>>();
        for (int i = 0; i < times; i++) {
            try {
                echoInfo.setMessage("hi" + i);

                Future<EchoInfo> echoInfo2 = echoService.echoAsync(echoInfo);
                list.add(echoInfo2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("begin wait");
        for (Future<EchoInfo> future : list) {
            EchoInfo echoInfo3;
            try {
                echoInfo3 = future.get(500000, TimeUnit.MILLISECONDS);
                System.out.println(echoInfo3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println(System.currentTimeMillis() - time);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
