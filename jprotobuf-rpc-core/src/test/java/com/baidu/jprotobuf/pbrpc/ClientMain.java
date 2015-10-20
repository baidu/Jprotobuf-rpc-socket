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
        options.setThreadPoolSize(5);
        options.setMaxIdleSize(10);
        options.setMaxWait(1000);
        
        RpcClient rpcClient = new RpcClient(options);
        ProtobufRpcProxy<EchoService> pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
        pbrpcProxy.setPort(1031);
        pbrpcProxy.setHost("localhost");
        EchoService echoService = pbrpcProxy.proxy();

        EchoInfo echoInfo = new EchoInfo();
        echoInfo.setMessage("hello");
        
        for (int i = 0; i < 10000000; i++) {
            echoService.echo(echoInfo);
        }
        
        pbrpcProxy.close();
        rpcClient.shutdown();
    }
}
