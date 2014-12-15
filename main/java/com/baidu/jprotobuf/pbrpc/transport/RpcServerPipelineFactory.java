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
package com.baidu.jprotobuf.pbrpc.transport;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageCompressHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageDecoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageEncoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageUnCompressHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcServerChannelIdleHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcServiceHandler;

/**
 * RPC server channel handler factory
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServerPipelineFactory implements ChannelPipelineFactory {

    /**
     * decode handler
     */
    private static final String DECODER = "decoder";
    
    private static final String UNCOMPRESS = "uncompress";
    private static final String COMPRESS = "compress";

    private static final String RPC_SERVER_HANDLER = "rpc_handler";

    private static Logger LOG = Logger.getLogger(RpcServerPipelineFactory.class.getName());

    private HashedWheelTimer idleTimer = new HashedWheelTimer();

    private final RpcServiceRegistry rpcServiceRegistry;

    private static final String RPC_CHANNEL_STATE_AWARE_HANDLER = "RpcChannelStateAwareHandler";

    private static final String RPC_CHANNEL_IDLE_HANDLER = "idel_channal_handler";

    private static final String SERVER_DATA_PACK = "server_data_pack";

    private final RpcServerOptions rpcServerOptions;
    
    private List<RpcDataPackageDecoder> rpcDataPackageDecoderList = new ArrayList<RpcDataPackageDecoder>();

    public RpcServerPipelineFactory(RpcServiceRegistry rpcServiceRegistry, RpcServerOptions rpcServerOptions) {
        this.rpcServiceRegistry = rpcServiceRegistry;
        this.rpcServerOptions = rpcServerOptions;
    }

    public ChannelPipeline getPipeline() throws Exception {
        LOG.log(Level.FINE, "begin process RPC server handler");
        ChannelPipeline channelPipe = pipeline();

        // receive request data
        channelPipe.addLast(
                RPC_CHANNEL_STATE_AWARE_HANDLER,
                new IdleStateHandler(this.idleTimer, this.rpcServerOptions.getKeepAliveTime(), this.rpcServerOptions
                        .getKeepAliveTime(), 0));
        channelPipe.addLast(RPC_CHANNEL_IDLE_HANDLER, new RpcServerChannelIdleHandler());
        
        
        RpcDataPackageDecoder rpcDataPackageDecoder = new RpcDataPackageDecoder(
                this.rpcServerOptions.getChunkPackageTimeout());
        rpcDataPackageDecoderList.add(rpcDataPackageDecoder);
        // receive byte array to encode to RpcDataPackage
        channelPipe.addLast(DECODER, rpcDataPackageDecoder);
        // do uncompress handle
        channelPipe.addLast(UNCOMPRESS, new RpcDataPackageUnCompressHandler());
        // to process RPC service handler of request object RpcDataPackage and
        // return new RpcDataPackage
        channelPipe.addLast(RPC_SERVER_HANDLER, new RpcServiceHandler(this.rpcServiceRegistry));
        
        // response back
        // check if need to compress for data and attachment
        channelPipe.addFirst(COMPRESS, new RpcDataPackageCompressHandler());
        // encode RpcDataPackage to byte array
        channelPipe.addFirst(SERVER_DATA_PACK, new RpcDataPackageEncoder());

        return channelPipe;
    }
    
    public void close() {
        if (rpcDataPackageDecoderList.isEmpty()) {
            return;
        }
        List<RpcDataPackageDecoder> list = new ArrayList<RpcDataPackageDecoder>(rpcDataPackageDecoderList);
        for (RpcDataPackageDecoder rpcDataPackageDecoder : list) {
            rpcDataPackageDecoder.close();
        }
    }

}
