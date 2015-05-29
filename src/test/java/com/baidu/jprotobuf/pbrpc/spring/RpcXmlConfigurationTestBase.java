/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baidu.jprotobuf.pbrpc.EchoInfo;
import com.baidu.jprotobuf.pbrpc.EchoService;

/**
 * Base RPC XML configuration test class.
 *
 * @author xiemalin
 * @since 2.17
 */
public abstract class RpcXmlConfigurationTestBase {

    /**
     * context of {@link AbstractApplicationContext}
     */
    protected AbstractApplicationContext context;
    


    @Before
    public void setUp() {
        context =
                new ClassPathXmlApplicationContext(getConfigurationPath());
    }
    
    protected abstract String getConfigurationPath();

    @After
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    protected void internalRpcRequestAndResponse(EchoService echoService) {
        EchoInfo echo = new EchoInfo();
        echo.setMessage("world");

        EchoInfo response = echoService.echo(echo);
        Assert.assertEquals("hello:world", response.getMessage());
    }
}
