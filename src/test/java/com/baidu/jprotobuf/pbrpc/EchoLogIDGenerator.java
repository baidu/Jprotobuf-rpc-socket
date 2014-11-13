/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

import java.util.concurrent.atomic.AtomicLong;

import com.baidu.jprotobuf.pbrpc.LogIDGenerator;

/**
 * Echo test {@link LogIDGenerator} instance.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class EchoLogIDGenerator implements LogIDGenerator {

    private static final AtomicLong SEQENCE = new AtomicLong(1);
    
    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.LogIDGenerator#generate(java.lang.String,
     * java.lang.String, java.lang.Object[])
     */
    public long generate(String serviceName, String methodName, Object... params) {
        return SEQENCE.incrementAndGet();
    }

}
