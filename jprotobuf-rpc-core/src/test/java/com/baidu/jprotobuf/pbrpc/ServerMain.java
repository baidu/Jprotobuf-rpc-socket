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

import com.baidu.jprotobuf.pbrpc.transport.RpcServer;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;

/**
 *
 * @author xiemalin
 *
 */
public class ServerMain {

    public static void main(String[] args) {
        
        if (args.length == 0) {
            args = new String[]{"8122"};
        }
        
        RpcServerOptions rpcServerOptions = new RpcServerOptions();
        rpcServerOptions.setHttpServerPort(8866);
        rpcServerOptions.setWorkThreads(50);
        
        RpcServer rpcServer = new RpcServer(rpcServerOptions);
        EchoServiceImpl echoServiceImpl = new EchoServiceImpl();
        rpcServer.registerService(echoServiceImpl);
        rpcServer.startSync(Integer.valueOf(args[0]));
        
        System.out.println("ok");
        
        
    }
}
