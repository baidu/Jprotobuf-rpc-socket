/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;


/**
 * RPC service exporter interface
 * 
 * @author xiemalin
 *
 * @param <I> input parameter
 * @param <O> response result
 */
public interface ServiceExporter<I, O> {
    
    /**
     * 
     * @return the service name
     */
    String getServiceName();
    
    /**
     * @return the method name
     */
    String getMethodName();

    /**
     * execute service action.
     * 
     * @param input
     * @return
     * @throws Exception
     */
    O execute(I input) throws Exception;
}
