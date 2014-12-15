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

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcMeta;
import com.baidu.jprotobuf.pbrpc.transport.BlockingRpcCallback;
import com.baidu.jprotobuf.pbrpc.transport.RpcChannel;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;
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

        RpcClientOptions options = new RpcClientOptions();
        options.setMinIdleSize(1);
        options.setThreadPoolSize(1);
        options.setMaxIdleSize(1);

        RpcClient rpcClient = new RpcClient(options);

        // build package
        RpcDataPackage dataPacage = new RpcDataPackage();
        dataPacage.magicCode(ProtocolConstant.MAGIC_CODE);
        dataPacage.serviceName("sn").methodName("method").data(new byte[] { 1, 2, 4, 8 });
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
            // System.out.println(Arrays.toString(callback.getMessage().getData()));
        }

    }
}
