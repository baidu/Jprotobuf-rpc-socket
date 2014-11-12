/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.data;

/**
 * Byte array serializable interface.
 *
 * @author xiemalin
 * @since 1.0
 */
public interface Readable {

    /**
     * Do deserializable action
     * @param bytes
     */
    void read(byte[] bytes);
}
