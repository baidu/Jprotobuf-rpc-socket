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

import com.baidu.jprotobuf.pbrpc.utils.SleepUtils;

/**
 * Echo service 
 * 
 * @author xiemalin
 * @since 1.0
 */
public class EchoServiceImpl {
    
    private Integer order;
    
    /**
     * 
     */
    public EchoServiceImpl() {
    }

    /**
     * @param order
     */
    public EchoServiceImpl(Integer order) {
        super();
        this.order = order;
    }


    @ProtobufRPCService(serviceName = "echoService", methodName = "echo", description ="echo测试服务")
    public EchoInfo doEcho(EchoInfo info) {
        EchoInfo ret = new EchoInfo();
        ret.setMessage("hello:" + info.getMessage() + (order == null ? "" : order));
        return ret;
    }
    

    @ProtobufRPCService(serviceName = "echoService", methodName = "echo2", description ="echo测试服务")
    public EchoInfo doEchoAnnother(EchoInfo info) {
        return doEcho(info);
    }

    @ProtobufRPCService(serviceName = "echoService", methodName = "echoWithAttachement", 
            attachmentHandler = EchoServerAttachmentHandler.class, description ="echo测试服务带附件")
    public EchoInfo dealWithAttachement(EchoInfo info) {
        return doEcho(info);
    }
    
    @ProtobufRPCService(serviceName = "echoService", methodName = "echoGzip", 
            attachmentHandler = EchoServerAttachmentHandler.class, description ="echo测试服务带附件和Gzip压缩")
    public EchoInfo dealWithGzipEnable(EchoInfo info) {
        return doEcho(info);
    }
    
    @ProtobufRPCService(serviceName = "echoService", methodName = "echoSnappy", 
            attachmentHandler = EchoServerAttachmentHandler.class, description ="echo测试服务带附件和Snappy压缩")
    public EchoInfo dealWithSnappyEnable(EchoInfo info) {
        return doEcho(info);
    }
    
    @ProtobufRPCService(serviceName = "echoService", methodName = "echoAuthenticateData", 
            attachmentHandler = EchoServerAttachmentHandler.class, description ="echo测试服务带附件和Snappy压缩",
            authenticationDataHandler = EchoServerAuthenticationDataHandler.class)
    public EchoInfo dealWithAuthenticationDataEnable(EchoInfo info) {
        return doEcho(info);
    }
    
    
    @ProtobufRPCService(serviceName = "echoService", methodName = "businessExceptionCall")
    public EchoInfo businessExceptionCall(EchoInfo info) {
        throw new RuntimeException("Throw business exception.");
    }
    
    @ProtobufRPCService(serviceName = "echoService", methodName = "echoTimeout", description ="echo测试服务")
    public EchoInfo echoTimeout(EchoInfo info) {
        SleepUtils.dummySleep(2000);
        EchoInfo ret = new EchoInfo();
        ret.setMessage("hello:" + info.getMessage());
        return ret;
    }
    
    public EchoInfo doEchoDynamic(EchoInfo info) {
        return info;
    }
}
