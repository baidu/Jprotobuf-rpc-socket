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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import com.baidu.jprotobuf.pbrpc.transport.handler.RpcClientServiceHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageCompressHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageDecoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageEncoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageUnCompressHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcServerChannelIdleHandler;

public class RpcClientPipelineFactory implements ChannelPipelineFactory {

    private static Logger LOG = Logger.getLogger(RpcClientPipelineFactory.class.getName());

    private static final String CLIENT_ENCODER = "client_data_encoder";
    private static final String CLIENT_DECODER = "client_data_decoder";

    private static final String RPC_CHANNEL_STATE_AWARE_HANDLER = "RpcChannelStateAwareHandler";
    private static final String RPC_CHANNEL_IDLE_HANDLER = "idel_channal_handler";

    private static final String CLIENT_HANDLER = "client_handler";

    private static final String COMPRESS = "compress_handler";
    private static final String UNCOMPRESS = "uncompress";

    private RpcClient rpcClient;
    private Timer idleTimer = new HashedWheelTimer();

    /**
     * @brief construct method
     * @param timer
     */
    public RpcClientPipelineFactory(RpcClient client) {
        this.rpcClient = client;

    }

    /**
     * @brief 产生处理器管道
     * @return ChannelPipeline
     * @throws Exception
     * @author songhuiqing
     * @date 2013/03/07 11:14:53
     */
    public ChannelPipeline getPipeline() throws Exception {
        LOG.log(Level.FINEST, "begin process RPC server response to client handler");
        ChannelPipeline channelPipe = pipeline();
        
        // to send data (top-down direction first call last)
        // idle state handle
        channelPipe.addFirst(RPC_CHANNEL_STATE_AWARE_HANDLER, new IdleStateHandler(this.idleTimer, this.rpcClient
                .getRpcClientOptions().getIdleTimeout(), this.rpcClient.getRpcClientOptions().getIdleTimeout(), 0));
        channelPipe.addFirst(RPC_CHANNEL_IDLE_HANDLER, new RpcServerChannelIdleHandler());
        
        // check if need to compress for data and attachment
        channelPipe.addFirst(COMPRESS, new RpcDataPackageCompressHandler());
        // encode RpcDataPackage to byte array
        channelPipe.addFirst(CLIENT_ENCODER, new RpcDataPackageEncoder(rpcClient.getRpcClientOptions().getChunkSize()));
        
        
        // receive data from server
        // receive byte array to encode to RpcDataPackage
        channelPipe.addLast(CLIENT_DECODER, new RpcDataPackageDecoder());
        // do uncompress handle
        channelPipe.addLast(UNCOMPRESS, new RpcDataPackageUnCompressHandler());
        // do client handler
        channelPipe.addLast(CLIENT_HANDLER, new RpcClientServiceHandler(rpcClient));
        
        return channelPipe;
    }
}
