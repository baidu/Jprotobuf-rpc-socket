/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

/**
 * RPC handle for each request and response
 * 
 * @author xiemalin
 * @since 1.0
 */
public interface RpcHandler {

    /**
     * send data to server
     * @param data
     * @exception Exception in case of any exception in handle
     */
    byte[] doHandle(byte[] data) throws Exception;

}
