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
package com.baidu.jprotobuf.pbrpc.client.ha;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.EchoInfo;
import com.baidu.jprotobuf.pbrpc.EchoService;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * Ha RpcProxy test.
 * 
 * @author xiemalin
 * @since 2.15
 */
public class HaEchoServiceTest extends HaEchoServiceTestBase {

    RpcClient rpcClient = new RpcClient();
    EchoService proxy;

    @Before
    public void setUp() {
        super.setUp();

        try {
            HaProtobufRpcProxy<EchoService> pbrpcProxy =
                    new HaProtobufRpcProxy<EchoService>(rpcClient, EchoService.class, getNamingService());

            int serverSize = getNamingService().list().size();
            Assert.assertEquals(5, serverSize);

            proxy = pbrpcProxy.proxy();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @After
    public void tearDown() {
        super.tearDown();
        rpcClient.stop();
    }

    @Test
    public void testNoServerFail() throws Exception {
        int serverSize = getNamingService().list().size();
        EchoInfo echoInfo = new EchoInfo("");
        Set<String> returnValues = new HashSet<String>();
        for (int i = 0; i < serverSize; i++) {
            EchoInfo echo = proxy.echo(echoInfo);
            Assert.assertFalse(returnValues.contains(echo.getMessage()));
            returnValues.add(echo.getMessage());
        }

        Assert.assertEquals(5, returnValues.size());

    }

    @Test
    public void testFailOverAndNoRecover() throws Exception {

        // first check all servers running ok
        testNoServerFail();

        // random stop one server
        stopOneServer();

        int serverSize = getNamingService().list().size();
        EchoInfo echoInfo = new EchoInfo("");
        Set<String> returnValues = new HashSet<String>();
        for (int i = 0; i < serverSize * 2; i++) {
            EchoInfo echo = proxy.echo(echoInfo);
            returnValues.add(echo.getMessage());
        }

        Assert.assertEquals(4, returnValues.size());

        // random stop one server
        stopOneServer();

        serverSize = getNamingService().list().size();
        returnValues = new HashSet<String>();
        for (int i = 0; i < serverSize * 2; i++) {
            EchoInfo echo = proxy.echo(echoInfo);
            returnValues.add(echo.getMessage());
        }

        Assert.assertEquals(3, returnValues.size());

    }

    @Test
    public void testFailOverAndRecover() throws Exception {

        // first check all servers running OK?
        testNoServerFail();

        // random stop one server
        testFailOverAndNoRecover();

        recoverServer();

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        int serverSize = getNamingService().list().size();
        EchoInfo echoInfo = new EchoInfo("");
        Set<String> returnValues = new HashSet<String>();
        for (int i = 0; i < serverSize * 2; i++) {
            EchoInfo echo = proxy.echo(echoInfo);
            returnValues.add(echo.getMessage());
        }

        Assert.assertEquals(5, returnValues.size());

    }

    @Test
    public void testDynamicServerListChanges() throws Exception {
        // first check all servers running OK?
        testNoServerFail();

        // delete one from naming service
        InetSocketAddress address = list.remove(0);
        try {
            Thread.sleep(1200);
        } catch (Exception e) {
        }
        // to check naming service get size
        int serverSize = getNamingService().list().size();
        Assert.assertEquals(4, serverSize);
        EchoInfo echoInfo = new EchoInfo("");
        Set<String> returnValues = new HashSet<String>();
        for (int i = 0; i < serverSize * 2; i++) {
            EchoInfo echo = proxy.echo(echoInfo);
            returnValues.add(echo.getMessage());
        }
        // after one RPC server removed
        Assert.assertEquals(4, returnValues.size());

        // put back to naming service
        list.add(address);
        try {
            Thread.sleep(1200);
        } catch (Exception e) {
        }
        // to check naming service get size
        serverSize = getNamingService().list().size();
        Assert.assertEquals(5, serverSize);

        returnValues = new HashSet<String>();
        for (int i = 0; i < serverSize * 2; i++) {
            EchoInfo echo = proxy.echo(echoInfo);
            returnValues.add(echo.getMessage());
        }
        // after one RPC server removed
        Assert.assertEquals(5, returnValues.size());
    }

}
