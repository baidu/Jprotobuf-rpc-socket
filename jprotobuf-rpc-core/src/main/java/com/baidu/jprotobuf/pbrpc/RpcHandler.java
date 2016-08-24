/*
 * Copyright 2002-2007 the original author or authors.
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
 * RPC handle for each request and response.
 *
 * @author xiemalin
 * @since 1.0
 */
public interface RpcHandler {

    /**
     * send data to server.
     *
     * @param data the data
     * @return the rpc data
     * @exception Exception in case of any exception in handle
     */
    RpcData doHandle(RpcData data) throws Exception;

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    String getServiceName();

    /**
     * Gets the method name.
     *
     * @return the method name
     */
    String getMethodName();

    /**
     * Gets the service.
     *
     * @return the service
     */
    Object getService();
    
    /**
     * Gets the input class.
     *
     * @return the input class
     */
    Class<?> getInputClass();
    
    /**
     * Gets the output class.
     *
     * @return the output class
     */
    Class<?> getOutputClass();
    
    
    /**
     * Gets the description.
     *
     * @return the description
     */
    String getDescription();
    
    /**
     * Gets the method signature.
     *
     * @return the method signature
     */
    String  getMethodSignature();

}
