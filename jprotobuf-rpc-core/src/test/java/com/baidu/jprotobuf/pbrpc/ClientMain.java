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

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;

/**
 *
 * @author xiemalin
 *
 */
public class ClientMain {

    public static void main(String[] args) {

        RpcClientOptions options = new RpcClientOptions();
        options.setThreadPoolSize(10);
        options.setMaxIdleSize(10);
        options.setMinIdleSize(10);
        options.setMaxWait(5000);
        options.setShortConnection(false);

        RpcClient rpcClient = new RpcClient(options);
        ProtobufRpcProxy<EchoService> pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
        pbrpcProxy.setPort(8000);
        pbrpcProxy.setHost("localhost");
        EchoService echoService = pbrpcProxy.proxy();

        EchoInfo echoInfo = new EchoInfo();
        long time = System.currentTimeMillis();
        List<Future<EchoInfo>> list = new ArrayList<Future<EchoInfo>>();
        for (int i = 0; i < 5000; i++) {
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
                echoInfo3 = future.get(5000, TimeUnit.MILLISECONDS);
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
        pbrpcProxy.close();
        rpcClient.shutdown();
    }
}
