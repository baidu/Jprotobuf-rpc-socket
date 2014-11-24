/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaService;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;

/**
 * Test class for {@link RpcServiceMetaService}
 *
 * @author xiemalin
 * @since 2.1
 */
public class RpcServiceMetaServiceTest extends BaseEchoServiceTest {

    @Test
    public void testGetRpcMethodMeta() {
        RpcClient rpcClient = new RpcClient();
        
        ProtobufRpcProxy<RpcServiceMetaService> pbrpcProxy = new ProtobufRpcProxy<RpcServiceMetaService>(rpcClient, RpcServiceMetaService.class);
        pbrpcProxy.setPort(PORT);
        RpcServiceMetaService proxy = pbrpcProxy.proxy();
        
        RpcServiceMetaList rpcServiceMetaInfo = proxy.getRpcServiceMetaInfo();
        Assert.assertEquals(4, rpcServiceMetaInfo.getRpcServiceMetas().size());
        
        List<RpcServiceMeta> rpcServiceMetas = rpcServiceMetaInfo.getRpcServiceMetas();
        for (RpcServiceMeta rpcServiceMeta : rpcServiceMetas) {
            System.out.println("-----------------------RPC service meta info------------------");
            System.out.println("serviceName:" + rpcServiceMeta.getServiceName());
            System.out.println("methodName:" + rpcServiceMeta.getMethodName());
            System.out.println("inputProto:" + rpcServiceMeta.getInputProto());
            System.out.println("outputProto:" + rpcServiceMeta.getOutputProto());
        }
    }
    
}
