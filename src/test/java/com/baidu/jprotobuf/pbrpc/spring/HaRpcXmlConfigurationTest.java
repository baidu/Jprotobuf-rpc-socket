/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.EchoService;

/**
 * Test class for {@link HaRpcServiceExporter} and {@link HaRpcProxyFactoryBean} by XML configuration.
 * 
 * @author xiemalin
 * @since 2.17
 */
public class HaRpcXmlConfigurationTest extends RpcXmlConfigurationTestBase {

    private RpcServiceExporter rpcServiceExporter1;
    private RpcServiceExporter rpcServiceExporter2;
    private RpcServiceExporter rpcServiceExporter3;

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.RpcXmlConfigurationTestBase#getConfigurationPath()
     */
    @Override
    protected String getConfigurationPath() {
        return "classpath:" + HaRpcXmlConfigurationTest.class.getName().replace('.', '/') + ".xml";
    }

    @Before
    public void setUp() {
        super.setUp();
        rpcServiceExporter1 = context.getBean("rpcServer1", RpcServiceExporter.class);
        rpcServiceExporter2 = context.getBean("rpcServer2", RpcServiceExporter.class);
        rpcServiceExporter3 = context.getBean("rpcServer3", RpcServiceExporter.class);
    }

    @Test
    public void testPartialServerFailed() throws Exception {
        
        EchoService echoService = context.getBean("echoServiceProxy", EchoService.class);
        
        // shutdown server1
        if (rpcServiceExporter1 != null) {
            rpcServiceExporter1.destroy();
        }

        super.internalRpcRequestAndResponse(echoService);

        // shutdown server2
        if (rpcServiceExporter2 != null) {
            rpcServiceExporter2.destroy();
        }
        super.internalRpcRequestAndResponse(echoService);
        
        // shutdown all servers
        if (rpcServiceExporter3 != null) {
            rpcServiceExporter3.destroy();
        }
        
        try {
            super.internalRpcRequestAndResponse(echoService);
            // should throw exception on no servers available
            Assert.fail("No servers available should throw exception");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        
        // recover server1
        if (rpcServiceExporter1 != null) {
            rpcServiceExporter1.afterPropertiesSet();
        }
        try {
            Thread.sleep(2500);
        } catch (Exception e) {
            // TODO: handle exception
        }
        // server1 recover should test ok
        super.internalRpcRequestAndResponse(echoService);
        
    }
}
