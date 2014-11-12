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
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageDecoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageEncoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcServerChannelIdleHandler;

public class RpcClientPipelineFactory implements ChannelPipelineFactory {

    private static Logger LOG = Logger.getLogger(RpcClientPipelineFactory.class.getName());

    private static final String CLIENT_ENCODER = "client_data_encoder";
    private static final String CLIENT_DECODER = "client_data_decoder";

    private static final String RPC_CHANNEL_STATE_AWARE_HANDLER = "RpcChannelStateAwareHandler";
    private static final String RPC_CHANNEL_IDLE_HANDLER = "idel_channal_handler";

    private static final String CLIENT_HANDLER = "client_handler";


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

        // encode RpcDataPackage to byte array
        channelPipe.addLast(CLIENT_ENCODER, new RpcDataPackageEncoder());
        
        // receive byte array to encode to RpcDataPackage
        channelPipe.addLast(CLIENT_DECODER, new RpcDataPackageDecoder());
        // do client handler
        channelPipe.addLast(CLIENT_HANDLER, new RpcClientServiceHandler(rpcClient));
        
        // idle state handle
        channelPipe.addLast(RPC_CHANNEL_STATE_AWARE_HANDLER, new IdleStateHandler(this.idleTimer, this.rpcClient
                .getRpcClientOptions().getIdleTimeout(), this.rpcClient.getRpcClientOptions().getIdleTimeout(), 0));
        channelPipe.addLast(RPC_CHANNEL_IDLE_HANDLER, new RpcServerChannelIdleHandler());
        
        return channelPipe;
    }
}
