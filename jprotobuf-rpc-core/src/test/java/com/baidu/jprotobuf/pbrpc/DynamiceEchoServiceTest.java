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

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.DynamicProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;

import junit.framework.Assert;

/**
 * 
 * Test for {@link EchoService} in export and proxy by dyanmic way.
 *
 * @author xiemalin
 * @since 1.0
 * @see EchoService
 * 
 */

public class DynamiceEchoServiceTest {

    @Test
    public void testDynamicRpcProxy() throws Throwable {
        String serviceSignature = "echo";

        int port = 8899;

        RpcServer rpcServer = new RpcServer();

        EchoServiceImpl echoImpl = new EchoServiceImpl();
        Method sMethod = EchoServiceImpl.class.getDeclaredMethod("doEchoDynamic", new Class[] { EchoInfo.class });

        rpcServer.registerDynamicService(serviceSignature, sMethod, echoImpl, DummyServerAttachmentHandler.class);
        rpcServer.start(port);

        RpcClient rpcClient = new RpcClient();

        DynamicProtobufRpcProxy proxy = new DynamicProtobufRpcProxy(rpcClient);
        proxy.setPort(port);

        Method method = EchoService.class.getMethod("echo", new Class[] { EchoInfo.class });
        EchoInfo echoInfo = getEchoInfo();
        Object[] args = new Object[] { echoInfo };
        EchoInfo echoInfo2 =
                (EchoInfo) proxy.invoke(serviceSignature, proxy, method, args, DummyClientAttachmentHandler.class);
        Assert.assertEquals(echoInfo2.getMessage(), echoInfo.getMessage());

        rpcServer.shutdown();
        
        proxy.close();
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

}
