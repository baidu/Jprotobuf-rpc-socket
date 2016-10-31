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
package com.baidu.jprotobuf.pbrpc.spring;

import org.springframework.stereotype.Service;

import com.baidu.jprotobuf.pbrpc.EchoService;
import com.baidu.jprotobuf.pbrpc.spring.annotation.HaRpcProxy;
import com.baidu.jprotobuf.pbrpc.spring.annotation.RpcProxy;

/**
 * Test class for @RpcProxy
 * 
 * @author xiemalin
 * @since 2.17
 */
@Service("echoServiceClient")
public class AnnotationEchoServiceClient {

    @RpcProxy(port = "1031", host = "127.0.0.1", serviceInterface = EchoService.class, 
            lookupStubOnStartup = false, rpcClientOptionsBeanName = "rpcClientOptions", invokerIntercepterBeanName = "annoClientInterceptor")
    public EchoService echoService;

    @HaRpcProxy(namingServiceBeanName = "namingService", serviceInterface = EchoService.class,
            lookupStubOnStartup = false, invokerIntercepterBeanName = "annoHaClientInterceptor")
    public EchoService haEchoService;

    @HaRpcProxy(namingServiceBeanName = "namingServiceOfPartialFailed", serviceInterface = EchoService.class,
            lookupStubOnStartup = false)
    public EchoService haEchoServiceOfPartialFailed;
    
    
    @HaRpcProxy(namingServiceBeanName = "namingService", serviceInterface = EchoService.class,
            lookupStubOnStartup = false, failoverInteceptorBeanName = "timeoutIngoredSocketFailOverInterceptor")
    public EchoService namingServiceOfTimeoutFailed;
    
    @RpcProxy(port = "1034", host = "127.0.0.1", serviceInterface = EchoService.class, 
            lookupStubOnStartup = false, rpcClientOptionsBeanName = "rpcClientOptions", invokerIntercepterBeanName = "failedAnnoClientInterceptor")
    public EchoService clientFailedInterceptor;
    
    @RpcProxy(port = "1034", host = "127.0.0.1", serviceInterface = EchoService.class, 
            lookupStubOnStartup = false, rpcClientOptionsBeanName = "rpcClientOptions")
    public EchoService serverFailedInterceptor;
}
