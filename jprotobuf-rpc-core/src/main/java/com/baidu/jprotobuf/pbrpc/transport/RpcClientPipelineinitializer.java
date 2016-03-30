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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.transport.handler.RpcClientServiceHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageCompressHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageDecoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageEncoder;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcDataPackageUnCompressHandler;
import com.baidu.jprotobuf.pbrpc.transport.handler.RpcServerChannelIdleHandler;

public class RpcClientPipelineinitializer extends ChannelInitializer<Channel> {

	private static Logger LOG = Logger.getLogger(RpcClientPipelineinitializer.class.getName());

	private static final String CLIENT_ENCODER = "client_data_encoder";
	private static final String CLIENT_DECODER = "client_data_decoder";

	private static final String RPC_CHANNEL_STATE_AWARE_HANDLER = "RpcChannelStateAwareHandler";
	private static final String RPC_CHANNEL_IDLE_HANDLER = "idel_channal_handler";

	private static final String CLIENT_HANDLER = "client_handler";

	private static final String COMPRESS = "compress_handler";
	private static final String UNCOMPRESS = "uncompress";

	private RpcClient rpcClient;

	/**
	 * @brief construct method
	 * @param timer
	 */
	public RpcClientPipelineinitializer(RpcClient client) {
		this.rpcClient = client;

	}

	/**
	 * @brief 产生处理器管道
	 * @return ChannelPipeline
	 * @throws Exception
	 */
	@Override
	protected void initChannel(Channel ch) throws Exception {
		LOG.log(Level.FINEST, "begin process RPC server response to client handler");
		ChannelPipeline channelPipe = ch.pipeline();
		int idleTimeout = this.rpcClient.getRpcClientOptions().getIdleTimeout();
		channelPipe.addFirst(RPC_CHANNEL_STATE_AWARE_HANDLER,
				new IdleStateHandler(idleTimeout, idleTimeout, idleTimeout));
		channelPipe.addFirst(RPC_CHANNEL_IDLE_HANDLER, new RpcServerChannelIdleHandler());

		// check if need to compress for data and attachment
		channelPipe.addFirst(COMPRESS, new RpcDataPackageCompressHandler());
		// encode RpcDataPackage to byte array
		channelPipe.addFirst(CLIENT_ENCODER, new RpcDataPackageEncoder(rpcClient.getRpcClientOptions().getChunkSize()));

		// receive data from server

		int messageLengthFieldStart = 4;
		int messageLengthFieldWidth = 4;
		// Head meta size is 12, so messageLengthFieldStart +
		// messageLengthFieldWidth + adjustSize = 12;
		int adjustSize = 4;
		channelPipe.addLast("frameDecoder",
				new LengthFieldBasedFrameDecoder(rpcClient.getRpcClientOptions().getMaxSize(), messageLengthFieldStart,
						messageLengthFieldWidth, adjustSize, 0));

		// receive byte array to encode to RpcDataPackage
		channelPipe.addLast(CLIENT_DECODER, new RpcDataPackageDecoder(-1));
		// do uncompress handle
		channelPipe.addLast(UNCOMPRESS, new RpcDataPackageUnCompressHandler());
		// do client handler
		channelPipe.addLast(CLIENT_HANDLER, new RpcClientServiceHandler(rpcClient));

	}
}
