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
