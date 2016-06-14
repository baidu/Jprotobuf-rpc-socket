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
 * @date 2013/03/07 10:30:20
 * @version 1.0.0
 */
public class RpcClient extends Bootstrap {

    /**
     * Tick count of each wheel instance for timer
     */
    private static final int DEFAULT_TICKS_PER_WHEEL = 2048;

    /**
     * Tick duration for timer
     */
    private static final int DEFAULT_TICK_DURATION = 100;

    // 会话状态存储
    private final Map<Long, RpcClientCallState> requestMap = new ConcurrentHashMap<Long, RpcClientCallState>();

    private AtomicLong correlationId = new AtomicLong(1); // session标识
    private static Timer timer = createTimer(); // 初始化定时器
    private RpcClientOptions rpcClientOptions;
    private ChannelPool channelPool;
    private EventLoopGroup workerGroup;

    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    private static Timer createTimer() {
        Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory(), DEFAULT_TICK_DURATION,
                TimeUnit.MILLISECONDS, DEFAULT_TICKS_PER_WHEEL);
        return timer;
    }

    public RpcClient() {
        this(NioSocketChannel.class);
    }

    public RpcClient(RpcClientOptions rpcClientOptions) {
        this(NioSocketChannel.class, rpcClientOptions);
    }

    public RpcClient(Class<? extends Channel> clientChannelClass) {
        this(NioSocketChannel.class, new RpcClientOptions());
    }

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
     * @brief 应用层删除用户请求状态
     * @param seqId
     * @return RpcClientCallState
     * @author songhuiqing
     * @date 2013/03/07 10:34:30
     */
    public RpcClientCallState removePendingRequest(long seqId) {
        return requestMap.remove(seqId);
    }

    /**
     * @brief 应用层注册用户请求状态
     * @param seqId
     * @param state
     * @return void
     * @exception IllegalArgumentException
     * @author songhuiqing
     * @date 2013/03/07 10:34:30
     */
    public void registerPendingRequest(long seqId, RpcClientCallState state) {
        if (requestMap.containsKey(seqId)) {
            throw new IllegalArgumentException("State already registered");
        }
        requestMap.put(seqId, state);
    }

    /**
     * @brief 生成会话标识
     * @return long
     * @author songhuiqing
     * @date 2013/03/07 10:33:20
     */
    public long getNextCorrelationId() {
        return correlationId.getAndIncrement();
    }

    public Timer getTimer() {
        return timer;
    }

    public RpcClientOptions getRpcClientOptions() {
        return rpcClientOptions;
    }

    public void setRpcClientOptions(RpcClientOptions rpcClientOptions) {
        this.rpcClientOptions = rpcClientOptions;
    }

    /**
     * get the channelPool
     * 
     * @return the channelPool
     */
    protected ChannelPool getChannelPool() {
        return channelPool;
    }

    /**
     * set channelPool value to channelPool
     * 
     * @param channelPool the channelPool to set
     */
    protected void setChannelPool(ChannelPool channelPool) {
        this.channelPool = channelPool;
    }

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
     * do shutdown action
     */
    public void stop() {
        shutdown();
    }

}
