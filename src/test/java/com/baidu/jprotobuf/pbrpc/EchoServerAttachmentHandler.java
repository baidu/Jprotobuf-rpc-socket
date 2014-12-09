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


import org.junit.Assert;

/**
 * Echo test {@link ServerAttachmentHandler}
 * 
 * @author xiemalin
 * @since 1.1
 */
public class EchoServerAttachmentHandler implements ServerAttachmentHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler#handleAttachement(byte
     * [], java.lang.String, java.lang.String, java.lang.Object[])
     */
    public byte[] handleAttachement(byte[] response, String serviceName, String methodName, Object... params) {
        Assert.assertEquals(EchoClientAttachmentHandler.class.getName(), new String(response));
        return EchoServerAttachmentHandler.class.getName().getBytes();
    }

}
