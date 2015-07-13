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


import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for RpcMeta
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcMetaTest {
    
    protected RpcMeta getRpcMeta() {
        RpcMeta meta = new RpcMeta();
        
        RpcRequestMeta rpcRequestMeta = new RpcRequestMeta();
        rpcRequestMeta.setServiceName("testServiceName");
        rpcRequestMeta.setMethodName("testMethodName");
        rpcRequestMeta.setLogId(11L);
        meta.setRequest(rpcRequestMeta);
        
        RpcResponseMeta rpcResponseMeta = new RpcResponseMeta();
        rpcResponseMeta.setErrorCode(10);
        rpcResponseMeta.setErrorText("this is error text");
        meta.setResponse(rpcResponseMeta);
        
        ChunkInfo info = new ChunkInfo();
        info.setChunkId(100L);
        info.setStreamId(-1L);
        meta.setChunkInfo(info);
        
        meta.setAttachmentSize(-10);
        byte[] authenticationData = new byte[] {1, 3, 5, 7};
        meta.setAuthenticationData(authenticationData);
        meta.setCompressType(RpcMeta.COMPERESS_GZIP);
        meta.setCorrelationId(101L);
        
        return meta;
    }

    @Test
    public void testReadWrite() {
        
        RpcMeta meta = getRpcMeta();
        
        byte[] bytes = meta.write();
        
        RpcMeta meta2 = new RpcMeta();
        meta2.read(bytes);
        
        assertEquals(meta, meta2);
    }

    /**
     * @param meta
     * @param meta2
     */
    protected void assertEquals(RpcMeta meta, RpcMeta meta2) {
        Assert.assertEquals(meta.getAttachmentSize(), meta2.getAttachmentSize());
        Assert.assertArrayEquals(meta.getAuthenticationData(), meta2.getAuthenticationData());
        Assert.assertEquals(meta.getCompressType(), meta2.getCompressType());
        Assert.assertEquals(meta.getCorrelationId(), meta2.getCorrelationId());
        Assert.assertEquals(meta.getChunkInfo().getChunkId(), meta2.getChunkInfo().getChunkId());
        Assert.assertEquals(meta.getChunkInfo().getStreamId(), meta2.getChunkInfo().getStreamId());
        Assert.assertEquals(meta.getRequest().getMethodName(), meta2.getRequest().getMethodName());
        Assert.assertEquals(meta.getRequest().getSerivceName(), meta2.getRequest().getSerivceName());
        Assert.assertEquals(meta.getRequest().getLogId(), meta2.getRequest().getLogId());
        
        Assert.assertEquals(meta.getResponse().getErrorText(), meta2.getResponse().getErrorText());
        Assert.assertEquals(meta.getResponse().getErrorCode(), meta2.getResponse().getErrorCode());
    }
}
 