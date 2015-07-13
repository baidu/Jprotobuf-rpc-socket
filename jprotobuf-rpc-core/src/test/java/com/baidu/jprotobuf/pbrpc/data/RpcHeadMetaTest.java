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
 * Test class for RpcHeadMeta
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcHeadMetaTest {

    @Test
    public void testReadWrite() {
        RpcHeadMeta meta = new RpcHeadMeta();
        meta.setMagicCode("HULU");
        meta.setMetaSize(0);
        meta.setMessageSize(12);
        
        byte[] bytes = meta.write();
        Assert.assertEquals(RpcHeadMeta.SIZE, bytes.length);
        
        RpcHeadMeta meta2 = new RpcHeadMeta();
        meta2.read(bytes);
        
        Assert.assertArrayEquals(meta.getMagicCode(), meta2.getMagicCode());
        Assert.assertEquals(meta.getMetaSize(), meta2.getMetaSize());
        Assert.assertEquals(meta.getMessageSize(), meta2.getMessageSize());
    }
}
