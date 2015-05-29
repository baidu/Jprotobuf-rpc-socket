/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationFactory;

import com.baidu.jprotobuf.pbrpc.EchoInfo;
import com.baidu.jprotobuf.pbrpc.EchoService;
import com.baidu.jprotobuf.pbrpc.EchoServiceImpl;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaService;

/**
 * 
 * Test for {@link RpcProxyFactoryBean} and {@link RpcServiceExporter}
 *
 * @author xiemalin
 * @since 2.1.0.0
 */
public class RpcProxyTest {
    
    private int servicePort = 1031;

    private RpcProxyFactoryBean rpcProxyFactoryBean;
    
    private RpcServiceExporter rpcServiceExporter;
    
    @Before
    public void setUp() throws Exception {
        
        rpcServiceExporter = new RpcServiceExporter();
        rpcServiceExporter.setServicePort(servicePort);
        
        EchoServiceImpl service = new EchoServiceImpl();
        rpcServiceExporter.setRegisterServices(new ArrayList<Object>(Arrays.asList(service)));
        
        rpcServiceExporter.setRemoteInvocationExecutor(new RemoteInvocationExecutor() {
            
            @Override
            public Object invoke(RemoteInvocation arg0, Object arg1) throws NoSuchMethodException, 
                    IllegalAccessException, InvocationTargetException {
                return arg0.invoke(arg1);
            }
        });
        rpcServiceExporter.afterPropertiesSet();
        
        // setup client
        rpcProxyFactoryBean = new RpcProxyFactoryBean();
        rpcProxyFactoryBean.setServiceInterface(EchoService.class);
        rpcProxyFactoryBean.setPort(servicePort);
        rpcProxyFactoryBean.setRemoteInvocationFactory(new RemoteInvocationFactory() {
            
            @Override
            public RemoteInvocation createRemoteInvocation(MethodInvocation arg0) {
                DefaultRemoteInvocationFactory ret = new DefaultRemoteInvocationFactory();
                RemoteInvocation ri = ret.createRemoteInvocation(arg0);
                ri.addAttribute("hello", "world");
                return ri;
            }
        });
        rpcProxyFactoryBean.afterPropertiesSet();
        
    }
    
    @After
    public void tearDown() {
        if (rpcServiceExporter != null) {
            try {
                rpcServiceExporter.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testRpcMetaQuery() throws Exception {
        RpcProxyFactoryBean proxyBean = new RpcProxyFactoryBean();
        proxyBean.setServiceInterface(RpcServiceMetaService.class);
        proxyBean.setPort(servicePort);
        
        proxyBean.afterPropertiesSet();
        
        Object object = proxyBean.getObject();
        Assert.assertTrue(object instanceof RpcServiceMetaService);
        
        RpcServiceMetaService rpcServiceMetaService = (RpcServiceMetaService) object;
        
        RpcServiceMetaList rpcServiceMetaInfo = rpcServiceMetaService.getRpcServiceMetaInfo();

        List<RpcServiceMeta> rpcServiceMetas = rpcServiceMetaInfo.getRpcServiceMetas();
        Assert.assertEquals(6, rpcServiceMetas.size());

        
    }
    
    @Test
    public void testClientSend() throws Exception {
        Object object = rpcProxyFactoryBean.getObject();
        Assert.assertTrue(object instanceof EchoService);
        
        EchoService echoService = (EchoService) object;
        
        EchoInfo echo = new EchoInfo();
        echo.setMessage("world");
        
        EchoInfo response = echoService.echo(echo);
        Assert.assertEquals("hello:world", response.getMessage());
        
    }
}
