/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * A factory for creating GlobalChannelPoolSharable objects.
 *
 * @author xiemalin
 * @since 3.5.20
 */
public abstract class AbstractChannelPoolSharableFactory implements ChannelPoolSharableFactory {
    
    protected abstract Map<String, RpcChannel> getRpcChannelMap();
    
    /**
     * Gets the host address.
     *
     * @param host the host
     * @param port the port
     * @return the host address
     */
    protected String getHostAddress(String host, int port) {
        InetSocketAddress address;
        if (host == null) {
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(host, port);
        }
        return address.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.transport.ChannelPoolSharableFactory#getOrCreateChannelPool(java.lang.String, int)
     */
    @Override
    public RpcChannel getOrCreateChannelPool(RpcClient rpcClient, String host, int port) {
        
        String hostAddress = getHostAddress(host, port);
        if (!getRpcChannelMap().containsKey(hostAddress)) {
            RpcChannel rpcChannel = new RpcChannel(rpcClient, host, port);
            getRpcChannelMap().put(hostAddress, rpcChannel);
            return rpcChannel;
        }
        
        return getRpcChannelMap().get(hostAddress);
    }

}
