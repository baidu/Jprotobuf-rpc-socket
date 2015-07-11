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

package com.baidu.jprotobuf.pbrpc;

import com.baidu.jprotobuf.pbrpc.server.RpcData;

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
    RpcData doHandle(RpcData data) throws Exception;

    /**
     * get service name
     * @return service name
     */
    String getServiceName();

    /**
     * get method name
     * @return method name
     */
    String getMethodName();

    /**
     * get service instance.
     * @return target service instance
     */
    Object getService();
    
    /**
     * @return input class or null if no parameter.
     */
    Class<?> getInputClass();
    
    /**
     * @return output class or null if is a void return.
     */
    Class<?> getOutputClass();
    
    
    /**
     * @return RPC description
     */
    String getDescription();
    
    String  getMethodSignature();

}
