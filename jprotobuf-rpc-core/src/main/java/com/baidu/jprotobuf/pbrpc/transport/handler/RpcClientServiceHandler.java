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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcResponseMeta;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientCallState;

/**
 * RPC client service handler upon receive response data from server.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientServiceHandler extends
		SimpleChannelInboundHandler<RpcDataPackage> {

	/** log this class. */
	private static final Logger LOG = Logger
			.getLogger(RpcClientServiceHandler.class.getName());

	/** RPC client. */
	private RpcClient rpcClient;

	/**
	 * Instantiates a new rpc client service handler.
	 *
	 * @param rpcClient the rpc client
	 */
	public RpcClientServiceHandler(RpcClient rpcClient) {
		this.rpcClient = rpcClient;
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			RpcDataPackage dataPackage) throws Exception {
		Long correlationId = dataPackage.getRpcMeta().getCorrelationId();
		RpcClientCallState state = rpcClient
				.removePendingRequest(correlationId);

		Integer errorCode = ErrorCodes.ST_SUCCESS;
		RpcResponseMeta response = dataPackage.getRpcMeta().getResponse();
		if (response != null) {
			errorCode = response.getErrorCode();
		}

		if (!ErrorCodes.isSuccess(errorCode)) {
			if (state != null) {
				state.handleFailure(errorCode, response.getErrorText());
			} else {
				ctx.fireChannelReadComplete();
				throw new Exception(response.getErrorText());
			}
		} else {
			if (state != null) {
				state.setDataPackage(dataPackage);
				state.handleResponse(state.getDataPackage());
			}
		}
		ctx.fireChannelReadComplete();
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOG.log(Level.SEVERE, cause.getCause().getMessage(), cause.getCause());
		ctx.close();
	}

}
