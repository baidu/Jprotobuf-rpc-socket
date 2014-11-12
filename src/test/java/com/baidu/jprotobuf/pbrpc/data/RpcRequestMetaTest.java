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
 * Test class for RpcRequestMeta
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcRequestMetaTest {

    @Test
    public void testReadWrite() {
        testReadWrite(false);
    }

    @Test
    public void testReadWriteFailed() {
        RpcRequestMeta rpcRequestMeta = new RpcRequestMeta();

        rpcRequestMeta.setServiceName("testServiceName");
        try {

            rpcRequestMeta.write();
            Assert.fail("should throw exception due to required field 'methodName'");
        } catch (Exception e) {
            // should failed due to required field methodName
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testReadWriteAll() {
        testReadWrite(true);
    }

    private void testReadWrite(boolean includeLogId) {
        RpcRequestMeta rpcRequestMeta = new RpcRequestMeta();

        rpcRequestMeta.setServiceName("testServiceName");
        rpcRequestMeta.setMethodName("testMethodName");
        if (includeLogId) {
            rpcRequestMeta.setLogId(101L);
        }

        byte[] bytes = rpcRequestMeta.write();

        RpcRequestMeta rpcRequestMeta2 = new RpcRequestMeta();
        rpcRequestMeta2.read(bytes);

        Assert.assertEquals(rpcRequestMeta.getSerivceName(), rpcRequestMeta2.getSerivceName());
        Assert.assertEquals(rpcRequestMeta.getMethodName(), rpcRequestMeta2.getMethodName());

        if (includeLogId) {
            Assert.assertEquals(rpcRequestMeta.getLogId(), rpcRequestMeta2.getLogId());
        } else {
            Assert.assertNull(rpcRequestMeta2.getLogId());
        }
    }
}
