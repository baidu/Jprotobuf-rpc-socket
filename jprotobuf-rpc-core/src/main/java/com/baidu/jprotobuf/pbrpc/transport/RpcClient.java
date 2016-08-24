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

package com.baidu.jprotobuf.pbrpc.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

/**
 * RPC client handler class.
 *
 * @author xiemalin
 * @author songhuiqing
 * @version 1.0.0
 * @date 2013/03/07 10:30:20
 */
public class RpcClient extends Bootstrap {

    /** Tick count of each wheel instance for timer. */
    private static final int DEFAULT_TICKS_PER_WHEEL = 2048;

    /** Tick duration for timer. */
    private static final int DEFAULT_TICK_DURATION = 100;

    /** The request map. */
    // 会话状态存储
    private final Map<Long, RpcClientCallState> requestMap = new ConcurrentHashMap<Long, RpcClientCallState>();

    /** The correlation id. */
    private AtomicLong correlationId = new AtomicLong(1); // session标识
    
    /** The timer. */
    private static Timer timer = createTimer(); // 初始化定时器
    
    /** The rpc client options. */
    private RpcClientOptions rpcClientOptions;
    
    /** The channel pool. */
    private ChannelPool channelPool;
    
    /** The worker group. */
    private EventLoopGroup workerGroup;

    /** The Constant INSTANCE_COUNT. */
    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    /**
     * Creates the timer.
     *
     * @return the timer
     */
    private static Timer createTimer() {
        Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory(), DEFAULT_TICK_DURATION,
                TimeUnit.MILLISECONDS, DEFAULT_TICKS_PER_WHEEL);
        return timer;
    }

    /**
     * Instantiates a new rpc client.
     */
    public RpcClient() {
        this(NioSocketChannel.class);
    }

    /**
     * Instantiates a new rpc client.
     *
     * @param rpcClientOptions the rpc client options
     */
    public RpcClient(RpcClientOptions rpcClientOptions) {
        this(NioSocketChannel.class, rpcClientOptions);
    }

    /**
     * Instantiates a new rpc client.
     *
     * @param clientChannelClass the client channel class
     */
    public RpcClient(Class<? extends Channel> clientChannelClass) {
        this(NioSocketChannel.class, new RpcClientOptions());
    }

    /**
     * Instantiates a new rpc client.
     *
     * @param clientChannelClass the client channel class
     * @param rpcClientOptions the rpc client options
     */
    public RpcClient(Class<? extends Channel> clientChannelClass, RpcClientOptions rpcClientOptions) {

        if (rpcClientOptions.getIoEventGroupType() == RpcClientOptions.POLL_EVENT_GROUP) {
            this.workerGroup = new NioEventLoopGroup(rpcClientOptions.getThreadPoolSize());
        } else {
            this.workerGroup = new EpollEventLoopGroup(rpcClientOptions.getThreadPoolSize());
        }

        this.group(workerGroup);
        this.channel(clientChannelClass);
        this.handler(new RpcClientPipelineinitializer(this));
        this.rpcClientOptions = rpcClientOptions;
        this.option(ChannelOption.SO_REUSEADDR, rpcClientOptions.isReuseAddress());
        this.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, rpcClientOptions.getConnectTimeout());
        this.option(ChannelOption.SO_SNDBUF, rpcClientOptions.getSendBufferSize());
        this.option(ChannelOption.SO_RCVBUF, rpcClientOptions.getSendBufferSize());
        this.option(ChannelOption.SO_KEEPALIVE, rpcClientOptions.isKeepAlive());
        this.option(ChannelOption.TCP_NODELAY, rpcClientOptions.getTcpNoDelay());
        this.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR,
                new DefaultMessageSizeEstimator(rpcClientOptions.getReceiveBufferSize()));

        // add count
        INSTANCE_COUNT.incrementAndGet();
    }

    /**
     * Removes the pending request.
     *
     * @author songhuiqing
     * @param seqId the seq id
     * @return RpcClientCallState
     * @brief 应用层删除用户请求状态
     * @date 2013/03/07 10:34:30
     */
    public RpcClientCallState removePendingRequest(long seqId) {
        return requestMap.remove(seqId);
    }

    /**
     * Register pending request.
     *
     * @author songhuiqing
     * @param seqId the seq id
     * @param state the state
     * @return void
     * @exception IllegalArgumentException the illegal argument exception
     * @brief 应用层注册用户请求状态
     * @date 2013/03/07 10:34:30
     */
    public void registerPendingRequest(long seqId, RpcClientCallState state) {
        if (requestMap.containsKey(seqId)) {
            throw new IllegalArgumentException("State already registered");
        }
        requestMap.put(seqId, state);
    }

    /**
     * Gets the next correlation id.
     *
     * @return the next correlation id
     */
    public long getNextCorrelationId() {
        return correlationId.getAndIncrement();
    }

    /**
     * Gets the timer.
     *
     * @return the timer
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Gets the rpc client options.
     *
     * @return the rpc client options
     */
    public RpcClientOptions getRpcClientOptions() {
        return rpcClientOptions;
    }

    /**
     * Sets the rpc client options.
     *
     * @param rpcClientOptions the new rpc client options
     */
    public void setRpcClientOptions(RpcClientOptions rpcClientOptions) {
        this.rpcClientOptions = rpcClientOptions;
    }

    /**
     * Gets the channel pool.
     *
     * @return the channel pool
     */
    protected ChannelPool getChannelPool() {
        return channelPool;
    }

    /**
     * Sets the channel pool.
     *
     * @param channelPool the new channel pool
     */
    protected void setChannelPool(ChannelPool channelPool) {
        this.channelPool = channelPool;
    }

    /**
     * Shutdown.
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.jboss.netty.bootstrap.Bootstrap#shutdown()
     */
    public void shutdown() {
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
        if (channelPool != null) {
            channelPool.stop();
        }

        // to check instance count
        int count = INSTANCE_COUNT.decrementAndGet();
        if (count == 0) { // no current instance count try to stop old
            if (timer != null) {
                timer.stop();

                // reset timer
                timer = createTimer(); // 初始化定时器
            }
        }
    }

    /**
     * do shutdown action.
     */
    public void stop() {
        shutdown();
    }

}
