/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

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
