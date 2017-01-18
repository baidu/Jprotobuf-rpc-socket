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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;
import com.baidu.jprotobuf.pbrpc.utils.SleepUtils;

import junit.framework.Assert;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.BaseEchoServiceTest#getRpcServerOptions()
     */
    @Override
    protected RpcServerOptions getRpcServerOptions() {

        RpcServerOptions rpcServerOptions = new RpcServerOptions();
        rpcServerOptions.setHttpServerPort(8866);
        rpcServerOptions.setIoEventGroupType(RpcServerOptions.POLL_EVENT_GROUP);

        return rpcServerOptions;
    }

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

        SleepUtils.dummySleep(3000);

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

    @Test
    public void testGzip() {
        EchoInfo echoInfo = getEchoInfo();

        EchoServiceImpl ecohImpl = new EchoServiceImpl();

        EchoInfo response = echoService.echoGzip(echoInfo);
        Assert.assertEquals(ecohImpl.dealWithGzipEnable(echoInfo).getMessage(), response.getMessage());
    }

    @Test
    public void testSnappy() {
        EchoInfo echoInfo = getEchoInfo();

        EchoServiceImpl ecohImpl = new EchoServiceImpl();

        EchoInfo response = echoService.echoSnappy(echoInfo);
        Assert.assertEquals(ecohImpl.dealWithSnappyEnable(echoInfo).getMessage(), response.getMessage());
    }

    @Test
    public void testAuthenticationData() {
        EchoInfo echoInfo = getEchoInfo();

        EchoServiceImpl ecohImpl = new EchoServiceImpl();

        EchoInfo response = echoService.echoAuthenticateData(echoInfo);
        Assert.assertEquals(ecohImpl.dealWithAuthenticationDataEnable(echoInfo).getMessage(), response.getMessage());
    }

    @Test
    public void testAyncCall() {
        EchoInfo echoInfo = getEchoInfo();

        EchoServiceImpl ecohImpl = new EchoServiceImpl();

        Future<EchoInfo> echoAsync = echoService.echoAsync(echoInfo);

        try {
            EchoInfo response = echoAsync.get();
            Assert.assertEquals(ecohImpl.doEcho(echoInfo).getMessage(), response.getMessage());
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        } catch (ExecutionException e) {
            Assert.fail(e.getMessage());
            e.printStackTrace();
        }

    }

}
