/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
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
