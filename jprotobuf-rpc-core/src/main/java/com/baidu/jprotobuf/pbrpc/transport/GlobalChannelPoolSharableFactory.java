/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for creating GlobalChannelPoolSharable objects.
 *
 * @author xiemalin
 * @since 3.5.20
 */
public class GlobalChannelPoolSharableFactory extends AbstractChannelPoolSharableFactory {

    /** The rpc channel map. */
    private static Map<String, RpcChannel> rpcChannelMap = new HashMap<String, RpcChannel>();

    protected Map<String, RpcChannel> getRpcChannelMap() {
        return rpcChannelMap;
    }

}
