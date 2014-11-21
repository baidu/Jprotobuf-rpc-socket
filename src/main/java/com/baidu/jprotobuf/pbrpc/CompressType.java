/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * Compress type
 *
 * @author xiemalin
 * @since 1.4
 * @see ProtobufPRC
 */
public enum CompressType {
    
    /**
     * No compress
     */
    NO(0),
    // Snappy(1),
    
    /**
     * GZIP compress
     */
    GZIP(2);
    
    private final int value;

    CompressType(int value) { this.value = value; }

    public int value() { return this.value; }

    
}
