package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Idle 连接检测处理类。
 *
 * @author xiemalin
 *
 */
@Sharable
public class RpcServerChannelIdleHandler extends ChannelDuplexHandler {

	private static Logger LOG = Logger
			.getLogger(RpcServerChannelIdleHandler.class.getName());
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.ALL_IDLE) {
				// if no read and write for period time, close current channel
				LOG.log(Level.WARNING, "channel:" + ctx.channel()
				+ " ip=" + ctx.channel().remoteAddress() + " is idle for period time. close now.");
				ctx.close();
			} else {
				LOG.log(Level.WARNING, "idle on channel[" + e.state() + "]:" + ctx.channel());
			}
		}
	}

	public RpcServerChannelIdleHandler() {
	}

}
