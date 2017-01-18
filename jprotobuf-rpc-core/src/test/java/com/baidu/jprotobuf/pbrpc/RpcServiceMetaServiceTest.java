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


import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
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
        Assert.assertEquals(8, rpcServiceMetaInfo.getRpcServiceMetas().size());
        
        List<RpcServiceMeta> rpcServiceMetas = rpcServiceMetaInfo.getRpcServiceMetas();
        for (RpcServiceMeta rpcServiceMeta : rpcServiceMetas) {
            System.out.println("-----------------------RPC service meta info------------------");
            System.out.println("serviceName:" + rpcServiceMeta.getServiceName());
            System.out.println("methodName:" + rpcServiceMeta.getMethodName());
            System.out.println("inputClass:" + rpcServiceMeta.getInputObjName());
            System.out.println("outputClass:" + rpcServiceMeta.getOutputObjName());
            System.out.println("inputProto:" + rpcServiceMeta.getInputProto());
            System.out.println("outputProto:" + rpcServiceMeta.getOutputProto());
        }
        
        System.out.println("-----------------------Grouped RPC service meta info------------------");
        System.out.println(rpcServiceMetaInfo.getTypesIDL());
        System.out.println(rpcServiceMetaInfo.getRpcsIDL());
    }
    
}
