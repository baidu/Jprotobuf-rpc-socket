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

import org.junit.Ignore;

import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;

/**
 * Test case for trunk data test
 *
 * @author xiemalin
 * @since 2.10
 */

public class TrunkEchoServiceTest extends EchoServiceTest {

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.BaseEchoServiceTest#getRpcClientOptions()
     */
    @Override
    protected RpcClientOptions getRpcClientOptions() {
        RpcClientOptions rpcClientOptions = new RpcClientOptions();
        rpcClientOptions.setChunkSize(5);
        rpcClientOptions.setShareThreadPoolUnderEachProxy(true);
        return rpcClientOptions;
    }
    
    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.BaseEchoServiceTest#getRpcServerOptions()
     */
    @Override
    protected RpcServerOptions getRpcServerOptions() {
        RpcServerOptions rpcServerOptions = new RpcServerOptions();
        rpcServerOptions.setChunkSize(3);
        return rpcServerOptions;
    }
}
