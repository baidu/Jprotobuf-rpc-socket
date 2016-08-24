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

package com.baidu.jprotobuf.pbrpc.server;


/**
 * RPC service exporter interface.
 *
 * @author xiemalin
 * @param <I> input parameter
 * @param <O> response result
 */
public interface ServiceExporter<I, O> {
    
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
     * execute service action.
     *
     * @param input the input
     * @return the o
     * @throws Exception the exception
     */
    O execute(I input) throws Exception;
}
