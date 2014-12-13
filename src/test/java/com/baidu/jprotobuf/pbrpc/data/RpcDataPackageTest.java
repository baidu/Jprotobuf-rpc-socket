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

package com.baidu.jprotobuf.pbrpc.data;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for RpcDataPackage
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcDataPackageTest {
    
    public RpcDataPackage getDataPackage() {
        RpcDataPackage rpcDataPackage = new RpcDataPackage();
        
        RpcMetaTest rpcMetaTest = new RpcMetaTest();
        RpcMeta rpcMeta = rpcMetaTest.getRpcMeta();
        rpcDataPackage.setRpcMeta(rpcMeta);
        
        rpcDataPackage.setMagicCode("HULU");
        
        rpcDataPackage.setData(new byte[] {1, 2, 4, 8});
        
        return rpcDataPackage;
    }

    @Test
    public void testReadWrite() {
        
        RpcDataPackage rpcDataPackage = new RpcDataPackage();
        
        RpcMetaTest rpcMetaTest = new RpcMetaTest();
        RpcMeta rpcMeta = rpcMetaTest.getRpcMeta();
        rpcDataPackage.setRpcMeta(rpcMeta);
        
        rpcDataPackage.setMagicCode("HULU");
        
        rpcDataPackage.setData(new byte[] {1, 2, 4, 8});
        
        byte[] bytes = rpcDataPackage.write();
        Assert.assertEquals(bytes.length, rpcDataPackage.getHead().getMessageSize() + RpcHeadMeta.SIZE);
        
        RpcDataPackage rpcDataPackage2 = new RpcDataPackage();
        rpcDataPackage2.read(bytes);
        
        rpcMetaTest.assertEquals(rpcMeta, rpcDataPackage2.getRpcMeta());
        
        Assert.assertEquals(rpcDataPackage.getHead().getMagicCodeAsString(), 
                rpcDataPackage2.getHead().getMagicCodeAsString());
        Assert.assertArrayEquals(rpcDataPackage.getData(), rpcDataPackage2.getData());
        
    }
    
    @Test
    public void testChunk2() {
        
        RpcDataPackage dataPackage = getDataPackage();
        
        dataPackage.data(dataPackage.write());
        
        int count = dataPackage.getData().length / 23 + 1;
        
        List<RpcDataPackage> chunkList = dataPackage.chunk(23);
        Assert.assertEquals(count, chunkList.size());
        
        RpcDataPackage mergedPackage = new RpcDataPackage();
        for (RpcDataPackage rpcDataPackage : chunkList) {
            mergedPackage.mergeData(rpcDataPackage.getData());
        }
        Assert.assertArrayEquals(dataPackage.getData(), mergedPackage.getData());
    }
    
    @Test
    public void testChunk() {
        RpcDataPackage dataPackage = getDataPackage();
        String serviceName = "hello";
        String methodName = "testMethod";
        dataPackage.serviceName(serviceName);
        dataPackage.methodName(methodName);
        byte[] attachment = new byte[] {1, 4};
        dataPackage.attachment(attachment);
        
        List<RpcDataPackage> chunkList = dataPackage.chunk(0);
        Assert.assertEquals(1, chunkList.size());
        
        chunkList = dataPackage.chunk(1);
        Assert.assertEquals(4, chunkList.size());
        
        int pos = 0;
        Long streamId = null;
        
        RpcDataPackage mergedPackage = new RpcDataPackage();
        
        for (RpcDataPackage data : chunkList) {
            Assert.assertEquals("HULU", data.getHead().getMagicCodeAsString());
            Assert.assertEquals(serviceName, data.getRpcMeta().getRequest().getSerivceName());
            Assert.assertEquals(methodName, data.getRpcMeta().getRequest().getMethodName());
            if (pos == 0) {
                Assert.assertArrayEquals(attachment, data.getAttachment());
                
            } else {
                Assert.assertNull(data.getAttachment());
            }
            
            if (streamId != null) {
                Assert.assertEquals(streamId, data.getRpcMeta().getChunkInfo().getStreamId());
            }
            streamId = data.getRpcMeta().getChunkInfo().getStreamId();
            if (pos == 3) {
                Assert.assertEquals(-1,  data.getRpcMeta().getChunkInfo().getChunkId());
            } else {
                Assert.assertEquals(pos,  data.getRpcMeta().getChunkInfo().getChunkId()); 
            }
            
            pos++;
            
            mergedPackage.mergeData(data.getData());
        }
        
        Assert.assertArrayEquals(dataPackage.getData(), mergedPackage.getData());
    }
    
}
