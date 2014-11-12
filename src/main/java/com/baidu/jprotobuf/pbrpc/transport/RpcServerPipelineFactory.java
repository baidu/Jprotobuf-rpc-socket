package com.baidu.jprotobuf.pbrpc.transport;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageDecoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageEncoder;
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
     * 
     */
    private static final String DECODER = "decoder";

    private static final String RPC_SERVER_HANDLER = "rpc_handler";

    private static Logger LOG = Logger.getLogger(RpcServerPipelineFactory.class.getName());

    private HashedWheelTimer idleTimer = new HashedWheelTimer();

    private final RpcServiceRegistry rpcServiceRegistry;

    private static final String RPC_CHANNEL_STATE_AWARE_HANDLER = "RpcChannelStateAwareHandler";

    private static final String RPC_CHANNEL_IDLE_HANDLER = "idel_channal_handler";

    private static final String SERVER_DATA_PACK = "server_data_pack";

    private final com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions rpcServerOptions;

    public RpcServerPipelineFactory(RpcServiceRegistry rpcServiceRegistry, RpcServerOptions rpcServerOptions) {
        this.rpcServiceRegistry = rpcServiceRegistry;
        this.rpcServerOptions = rpcServerOptions;
    }

    public ChannelPipeline getPipeline() throws Exception {
        LOG.log(Level.FINE, "begin process RPC server handler");
        ChannelPipeline channelPipe = pipeline();

        // receive byte array to encode to RpcDataPackage
        channelPipe.addLast(DECODER, new RpcDataPackageDecoder());
        // to process RPC service handler of request object RpcDataPackage and
        // return new RpcDataPackage
        channelPipe.addLast(RPC_SERVER_HANDLER, new RpcServiceHandler(this.rpcServiceRegistry));
        // encode RpcDataPackage to byte array
        channelPipe.addLast(SERVER_DATA_PACK, new RpcDataPackageEncoder());

        channelPipe.addLast(
                RPC_CHANNEL_STATE_AWARE_HANDLER,
                new IdleStateHandler(this.idleTimer, this.rpcServerOptions.getKeepAliveTime(), this.rpcServerOptions
                        .getKeepAliveTime(), 0));
        channelPipe.addLast(RPC_CHANNEL_IDLE_HANDLER, new RpcServerChannelIdleHandler());
        return channelPipe;
    }

}
