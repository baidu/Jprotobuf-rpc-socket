/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * @file RpcClient.java
 * @brief 应用层
 * @author songhuiqing
 * @date 2013/03/07 10:30:20
 * @version 1.0.0
 */
public class RpcClient extends ClientBootstrap {

    // 会话状态存储
    private final Map<Long, RpcClientCallState> requestMap = new ConcurrentHashMap<Long, RpcClientCallState>(); 
    
    private AtomicLong correlationId = new AtomicLong(1); // session标识
    private Timer timer = new HashedWheelTimer(); // 初始化定时器
    private RpcClientOptions rpcClientOptions;
    private ChannelPool channelPool;

    public RpcClient() {
        this(new NioClientSocketChannelFactory());
    }
    
    public RpcClient(RpcClientOptions rpcClientOptions) {
        this(new NioClientSocketChannelFactory(), rpcClientOptions);
    }

    public RpcClient(ChannelFactory channelFactory) {
        this(channelFactory, new RpcClientOptions());
    }

    public RpcClient(ChannelFactory channelFactory, RpcClientOptions rpcClientOptions) {

        super(channelFactory);
        setPipelineFactory(new RpcClientPipelineFactory(this));
        this.rpcClientOptions = rpcClientOptions;
        this.setOption("reuseAddress", rpcClientOptions.isReuseAddress());
        this.setOption("connectTimeoutMillis", rpcClientOptions.getConnectTimeout());
        this.setOption("sendBufferSize", rpcClientOptions.getSendBufferSize());
        this.setOption("receiveBufferSize", rpcClientOptions.getReceiveBufferSize());
        this.setOption("keepAlive", rpcClientOptions.isKeepAlive());
        this.setOption("tcpNoDelay", rpcClientOptions.getTcpNoDelay());
        this.setOption("receiveBufferSizePredictorFactory",
                new FixedReceiveBufferSizePredictorFactory(rpcClientOptions.getReceiveBufferSize()));

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
     * @return the channelPool
     */
    protected ChannelPool getChannelPool() {
        return channelPool;
    }

    /**
     * set channelPool value to channelPool
     * @param channelPool the channelPool to set
     */
    protected void setChannelPool(ChannelPool channelPool) {
        this.channelPool = channelPool;
    }
    
    public void stop() {
        if (channelPool != null) {
            channelPool.stop();
        }
        if (timer != null) {
            timer.stop();
        }
        shutdown();
    }

}
