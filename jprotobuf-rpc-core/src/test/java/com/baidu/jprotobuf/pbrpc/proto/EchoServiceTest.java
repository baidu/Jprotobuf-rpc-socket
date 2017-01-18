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

package com.baidu.jprotobuf.pbrpc.proto;

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.proto.EchoInfoClass.EchoInfo;

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
        EchoInfo echoInfo = EchoInfo.newBuilder().setMessage(message).build();
        return echoInfo;
    }
    
    @Test
    public void testAttachment() {
        EchoInfo echoInfo = getEchoInfo();
        
        EchoServiceImpl ecohImpl = new EchoServiceImpl();
        
        EchoInfo response = echoService.echoWithAttachement(echoInfo);
        Assert.assertEquals(ecohImpl.doEcho(echoInfo).getMessage(), response.getMessage());
    }

}
