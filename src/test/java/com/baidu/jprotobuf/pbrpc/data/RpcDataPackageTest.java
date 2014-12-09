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
