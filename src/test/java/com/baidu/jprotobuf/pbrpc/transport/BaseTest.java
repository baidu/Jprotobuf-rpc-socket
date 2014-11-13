/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

/**
 * Base test class
 *
 * @author xiemalin
 * @since 1.0
 */
public abstract class BaseTest {
    protected static final String HOST = System.getProperty("host", "127.0.0.1");
    protected static final int PORT = Integer.parseInt(System.getProperty("port", "1031"));
}
