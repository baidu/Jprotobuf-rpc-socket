/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.jprotobuf.pbrpc.transport;

/**
 * A factory for creating ChannelSharable objects.
 *
 * @author xiemalin
 * @since 3.5.20
 */
public interface ChannelPoolSharableFactory {

    /**
     * Gets the or create channel pool.
     *
     * @param rpcClient the rpc client
     * @param host the host
     * @param port the port
     * @return the or create channel pool
     */
    RpcChannel getOrCreateChannelPool(RpcClient rpcClient, String host, int port);
}
