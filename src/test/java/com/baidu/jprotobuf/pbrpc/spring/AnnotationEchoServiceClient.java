/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
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

    @RpcProxy(port = "1031", host = "127.0.0.1", serviceInterface = EchoService.class, lookupStubOnStartup = false)
    public EchoService echoService;

    @HaRpcProxy(namingServiceBeanName = "namingService", serviceInterface = EchoService.class,
            lookupStubOnStartup = false)
    public EchoService haEchoService;

    @HaRpcProxy(namingServiceBeanName = "namingServiceOfPartialFailed", serviceInterface = EchoService.class,
            lookupStubOnStartup = false)
    public EchoService haEchoServiceOfPartialFailed;
}
