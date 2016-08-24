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

package com.baidu.jprotobuf.pbrpc.meta;

/**
 * To get Rpc service method description.
 *
 * @author xiemalin
 * @since 2.1
 */
public interface RpcMetaAware {

    /**
     * Gets the input meta proto.
     *
     * @return the input meta proto
     */
    String getInputMetaProto();
    
    /**
     * Gets the output meta proto.
     *
     * @return the output meta proto
     */
    String getOutputMetaProto();
    
}
