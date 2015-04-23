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
package com.baidu.jprotobuf.pbrpc.compress;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Base test class for compress uitilty class.
 *
 * @author xiemalin
 * @since 2.13
 */
public abstract class BaseCompressTest {

    protected abstract Compress createCompress();
    
    @Test
    public void testSimpleChineseCompress() throws UnsupportedEncodingException, IOException {
        
        String simpleCHNString = "中国欢迎你！";
        
        assertResult(simpleCHNString);
    }
    
    private void assertResult(String string) throws UnsupportedEncodingException, IOException {
        Compress compress = createCompress();
        byte[] compressedByteArray = compress.compress(string.getBytes("utf-8"));
        
        byte[] unCompressString = compress.unCompress(compressedByteArray);
        Assert.assertEquals(string, new String(unCompressString, "utf-8"));
    }
    
    @Test
    public void testMixString() throws UnsupportedEncodingException, IOException {
        
        String simpleCHNString = "中国欢迎你！ hello world to china 中国. 123 !@#$%^&*()";
        
        assertResult(simpleCHNString);
    }
    
    @Test
    public void testLongMixString() throws UnsupportedEncodingException, IOException {
        
        String simpleCHNString = "中国欢迎你！ hello world to china 中国. 123 !@#$%^&*()";
        
        int repeat = 1000;
        StringBuilder longText = new StringBuilder(simpleCHNString.length() * repeat);
        for (int i = 0; i < repeat; i++) {
            longText.append(simpleCHNString);
        }
        
        assertResult(longText.toString());
    }
}
