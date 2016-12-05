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
package com.baidu.jprotobuf.pbrpc.management;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.transport.RpcServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * HTTP server controller.
 *
 * @author xiemalin
 * @since 3.1.0
 */
public class HttpServer extends ServerBootstrap {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(HttpServer.class.getName());

	/** The Constant DEFAULT_WAIT_STOP_INTERVAL. */
	private static final int DEFAULT_WAIT_STOP_INTERVAL = 200;

	/** The boss group. */
	private EventLoopGroup bossGroup;
	
	/** The worker group. */
	private EventLoopGroup workerGroup;
	
	/** The channel. */
	private Channel channel;

	/** The stop. */
	private AtomicBoolean stop = new AtomicBoolean(false);

	/** The handler. */
	private HttpServerInboundHandler handler;

	

	/**
	 * Instantiates a new http server.
	 *
	 * @param rpcServer the rpc server
	 */
	public HttpServer(final RpcServer rpcServer) {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();

		handler = new HttpServerInboundHandler(rpcServer);
		
		group(bossGroup, workerGroup);
		
		channel(NioServerSocketChannel.class);
		
		this.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
							.addLast("IDLE_HANDLER", new IdleStateHandler(rpcServer.getRpcServerOptions().getReaderIdleTime(),
									rpcServer.getRpcServerOptions().getWriterIdleTime(),
									rpcServer.getRpcServerOptions().getKeepAliveTime()));

					// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
					ch.pipeline().addLast("HTTP_RESPONSE", new HttpResponseEncoder());
					// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
					ch.pipeline().addLast("HTTP_REQUEST", new HttpRequestDecoder());
					ch.pipeline().addLast("STAUTS_NADLER", handler);

			}
		});
	}

	/**
	 * Start.
	 *
	 * @param port the port
	 */
	public void start(int port) {

		bind(port).addListener(new ChannelFutureListener() {

			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					channel = future.channel();
					// TODO notifyStarted();
				} else {
					// TODO notifyFailed(future.cause());
				}
			}
		});

		LOG.log(Level.INFO, "Http starting at port: " + port);
	}

	/**
	 * Wait for stop.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	public void waitForStop() throws InterruptedException {
		while (!stop.get()) {
			Thread.sleep(DEFAULT_WAIT_STOP_INTERVAL);
		}
		stop();
	}

	/**
	 * Stop.
	 */
	public void stop() {
		stop.compareAndSet(false, true);
	}

	/**
	 * Shutdown now.
	 */
	public void shutdownNow() {
		stop();
		if (channel != null && channel.isOpen()) {
			channel.close();
		}

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();

		if (handler != null) {
			handler.close();
		}
	}

}
