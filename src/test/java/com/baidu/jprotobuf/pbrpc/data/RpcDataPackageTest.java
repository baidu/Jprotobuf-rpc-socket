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
}
