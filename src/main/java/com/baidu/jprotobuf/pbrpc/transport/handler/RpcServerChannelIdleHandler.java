package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.net.SocketTimeoutException;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultExceptionEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RpcServerChannelIdleHandler extends IdleStateAwareChannelHandler {

    private static Logger LOG = Logger.getLogger(RpcServerChannelIdleHandler.class.getName());

    public RpcServerChannelIdleHandler() {
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        if (e.getState() == IdleState.WRITER_IDLE) {
            LOG.log(Level.WARNING, "write idle on channel:" + e.getChannel().getId());
        } else if (e.getState() == IdleState.READER_IDLE) {

            LOG.log(Level.WARNING, "channel:" + e.getChannel().getId() + " is time out." + e.getChannel());

            handleUpstream(ctx, new DefaultExceptionEvent(e.getChannel(), new SocketTimeoutException(
                    "force to close channel(" + ctx.getChannel().getRemoteAddress() + "), reason: time out.")));

            e.getChannel().close();

        }
        super.channelIdle(ctx, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.netty.channel.SimpleChannelHandler#exceptionCaught(org.jboss
     * .netty.channel.ChannelHandlerContext,
     * org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LOG.log(Level.SEVERE, e.getChannel().toString(), e.getCause());
        super.exceptionCaught(ctx, e);
    }
}
