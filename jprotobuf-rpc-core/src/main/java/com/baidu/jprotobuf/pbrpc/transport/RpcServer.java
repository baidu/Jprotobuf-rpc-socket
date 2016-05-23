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

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.management.HttpServer;
import com.baidu.jprotobuf.pbrpc.server.IDLServiceExporter;
import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * RPC server provider by Netty server.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServer extends ServerBootstrap {

    /**
     * 
     */
    private static final int DEFAULT_WAIT_STOP_INTERVAL = 200;

    private static final Logger LOG = Logger.getLogger(RpcServer.class.getName());

    private AtomicBoolean stop = new AtomicBoolean(false);

    private RpcServerOptions rpcServerOptions;

    private RpcServerPipelineInitializer rpcServerPipelineInitializer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    private InetSocketAddress inetSocketAddress;

    private long startTime;

    private HttpServer httpServer;

    private BlockingQueue<Runnable> blockingqueue = new LinkedBlockingQueue<Runnable>();
    private ThreadPoolExecutor es;

    /**
     * set interceptor value to interceptor
     * 
     * @param interceptor the interceptor to set
     */
    public void setInterceptor(InvokerInterceptor interceptor) {
        if (rpcServiceRegistry != null) {
            rpcServiceRegistry.setInterceptor(interceptor);
        }
    }

    /**
     * get the inetSocketAddress
     * 
     * @return the inetSocketAddress
     */
    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    /**
     * get the es
     * 
     * @return the es
     */
    public ThreadPoolExecutor getEs() {
        return es;
    }

    /**
     * rpcServiceRegistry
     */
    private RpcServiceRegistry rpcServiceRegistry;

    public RpcServer(Class<? extends ServerChannel> serverChannelClass, RpcServerOptions serverOptions,
            RpcServiceRegistry rpcServiceRegistry) {
        if (rpcServiceRegistry == null) {
            throw new RuntimeException("protperty 'rpcServiceRegistry ' is null.");
        }

        if (serverOptions == null) {
            serverOptions = new RpcServerOptions();
        }

        if (serverOptions.getIoEventGroupType() == RpcServerOptions.POLL_EVENT_GROUP) {
            this.bossGroup = new NioEventLoopGroup(serverOptions.getAcceptorThreads());
            this.workerGroup = new NioEventLoopGroup(serverOptions.getWorkThreads());
        } else {
            this.bossGroup = new EpollEventLoopGroup(serverOptions.getAcceptorThreads());
            this.workerGroup = new EpollEventLoopGroup(serverOptions.getWorkThreads());
        }

        es = new ThreadPoolExecutor(serverOptions.getTaskTheads(), serverOptions.getTaskTheads(), 60L, TimeUnit.SECONDS,
                blockingqueue);

        this.group(this.bossGroup, this.workerGroup);
        this.channel(serverChannelClass);

        this.option(ChannelOption.SO_BACKLOG, serverOptions.getBacklog());

        this.childOption(ChannelOption.SO_KEEPALIVE, serverOptions.isKeepAlive());
        this.childOption(ChannelOption.SO_REUSEADDR, true);
        this.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.childOption(ChannelOption.TCP_NODELAY, serverOptions.isTcpNoDelay());
        this.childOption(ChannelOption.SO_LINGER, serverOptions.getSoLinger());
        this.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverOptions.getConnectTimeout());
        this.childOption(ChannelOption.SO_RCVBUF, serverOptions.getReceiveBufferSize());
        this.childOption(ChannelOption.SO_SNDBUF, serverOptions.getSendBufferSize());

        this.rpcServiceRegistry = rpcServiceRegistry;
        // do register meta service
        rpcServiceRegistry.doRegisterMetaService();
        this.rpcServerOptions = serverOptions;
        this.rpcServerPipelineInitializer = new RpcServerPipelineInitializer(rpcServiceRegistry, rpcServerOptions, es);
        this.childHandler(rpcServerPipelineInitializer);
    }

    public RpcServer(RpcServerOptions serverOptions) {
        this(NioServerSocketChannel.class, serverOptions, new RpcServiceRegistry());
    }

    public RpcServer(RpcServerOptions serverOptions, RpcServiceRegistry rpcServiceRegistry) {
        this(NioServerSocketChannel.class, serverOptions, rpcServiceRegistry);
    }

    public RpcServer() {
        this(new RpcServerOptions());
    }

    public RpcServer(Class<? extends ServerChannel> serverChannelClass) {
        this(serverChannelClass, new RpcServerOptions(), new RpcServiceRegistry());
    }

    public void registerService(IDLServiceExporter service) {
        rpcServiceRegistry.registerService(service);
    }

    public void registerService(final Object target) {
        rpcServiceRegistry.registerService(target);
    }

    public void start(int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        start(inetSocketAddress);
    }

    public void start(InetSocketAddress sa) {
        LOG.log(Level.INFO, "RPC starting at: " + sa);
        this.bind(sa).addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel = future.channel();
                    // TODO notifyStarted();
                } else {
                    // TODO notifyFailed(future.cause());
                }
            }
        });
        this.inetSocketAddress = sa;

        startTime = System.currentTimeMillis();

        // check if need start http server
        if (rpcServerOptions.getHttpServerPort() > 0) {
            httpServer = new HttpServer(this);
            httpServer.start(rpcServerOptions.getHttpServerPort());
        }
    }

    public void waitForStop() throws InterruptedException {
        while (!stop.get()) {
            Thread.sleep(DEFAULT_WAIT_STOP_INTERVAL);
        }
        shutdown();
    }

    public void stop() {
        stop.set(true);
    }

    public AtomicBoolean getStop() {
        return stop;
    }

    public boolean isStop() {
        return stop.get();
    }

    public void shutdown() {
        stop();
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        es.shutdown();

        if (httpServer != null) {
            httpServer.shutdownNow();
        }

    }

    public void setStop(AtomicBoolean stop) {
        this.stop = stop;
    }

    /**
     * get the rpcServerOptions
     * 
     * @return the rpcServerOptions
     */
    public RpcServerOptions getRpcServerOptions() {
        return rpcServerOptions;
    }

    /**
     * set rpcServerOptions value to rpcServerOptions
     * 
     * @param rpcServerOptions the rpcServerOptions to set
     */
    public void setRpcServerOptions(RpcServerOptions rpcServerOptions) {
        this.rpcServerOptions = rpcServerOptions;
    }

    /**
     * get the startTime
     * 
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }
}
