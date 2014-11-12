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
 * Test class for RpcResponseMeta
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcResponseMetaTest {

    @Test
    public void testReadWrite() {
        RpcResponseMeta meta = new RpcResponseMeta();
        
        meta.setErrorCode(10);
        byte[] bytes = meta.write();
        
        RpcResponseMeta meta2 = new RpcResponseMeta();
        meta2.read(bytes);
        
        Assert.assertEquals(meta.getErrorCode(), meta2.getErrorCode());
        Assert.assertNull(meta2.getErrorText());
    }
    
    @Test
    public void testReadWriteAll() {
        RpcResponseMeta meta = new RpcResponseMeta();
        
        meta.setErrorCode(10);
        meta.setErrorText("this is error text");
        byte[] bytes = meta.write();
        
        RpcResponseMeta meta2 = new RpcResponseMeta();
        meta2.read(bytes);
        
        Assert.assertEquals(meta.getErrorCode(), meta2.getErrorCode());
        Assert.assertEquals(meta.getErrorText(), meta2.getErrorText());
    }
}
