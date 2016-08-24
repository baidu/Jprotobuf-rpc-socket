/*
 * Copyright 2002-2007 the original author or authors.
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
package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Idle 连接检测处理类。.
 *
 * @author xiemalin
 */
@Sharable
public class RpcServerChannelIdleHandler extends ChannelDuplexHandler {

	/** The log. */
	private static Logger LOG = Logger
			.getLogger(RpcServerChannelIdleHandler.class.getName());
	
	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
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

	/**
	 * Instantiates a new rpc server channel idle handler.
	 */
	public RpcServerChannelIdleHandler() {
	}

}
