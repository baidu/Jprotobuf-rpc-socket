/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.EchoService;


/**
 * Test class for {@link RpcServiceExporter} and {@link RpcProxyFactoryBean} by XML configuration.
 * 
 * @author xiemalin
 * @since 2.17
 */
public class RpcXmlConfigurationTest extends RpcXmlConfigurationTestBase {


    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.RpcXmlConfigurationTestBase#getConfigurationPath()
     */
    @Override
    protected String getConfigurationPath() {
        return "classpath:" + RpcXmlConfigurationTest.class.getName().replace('.', '/') + ".xml";
    }
    
    
    @Test
    public void testRpcRequestAndResponse() {
        EchoService echoService = context.getBean("echoServiceProxy", EchoService.class);
        super.internalRpcRequestAndResponse(echoService);
    }
}
