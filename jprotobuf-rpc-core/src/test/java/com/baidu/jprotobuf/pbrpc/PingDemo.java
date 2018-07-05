/*
 * Copyright 2002-2007 the original author or authors.
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
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaServiceProvider;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * The Class PingDemo.
 *
 * @author xiemalin
 */
public class PingDemo {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();

        ProtobufRpcProxy<PingService> pbrpcProxy = new ProtobufRpcProxy<PingService>(rpcClient, PingService.class);
        pbrpcProxy.setPort(808); // to change port

        PingService pingService = pbrpcProxy.proxy();
        
        // test success if
        pingService.ping();
    }

    /**
     * The Interface PingService.
     */
    public static interface PingService {
        
        /**
         * Ping.
         */
        @ProtobufRPC(serviceName = RpcServiceMetaServiceProvider.RPC_META_SERVICENAME,
                onceTalkTimeout = 3000,
                compressType = CompressType.NO)
        void ping();
    }
}
