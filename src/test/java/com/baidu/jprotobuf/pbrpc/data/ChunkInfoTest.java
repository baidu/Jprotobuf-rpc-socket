/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.data;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Test class for ChunkInfo
 *
 * @author xiemalin
 * @since 1.0
 */
public class ChunkInfoTest {

    @Test
    public void testReadWrite() {
        ChunkInfo info = new ChunkInfo();
        info.setChunkId(100L);
        info.setStreamId(-1L);
        
        byte[] bytes = info.write();
        
        ChunkInfo info2 = new ChunkInfo();
        info2.read(bytes);
        
        Assert.assertEquals(info.getChunkId(), info2.getChunkId());
        Assert.assertEquals(info.getStreamId(), info2.getStreamId());
    }
}
