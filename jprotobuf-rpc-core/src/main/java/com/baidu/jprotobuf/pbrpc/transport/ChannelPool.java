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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Adapter for netty channel. Used by Mcpack Netty Client {@link NettyClient}.
 * 
 * @author xuyuepeng, sunzhongyi, lijianbin
 * 
 */
public class ChannelPool {
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(ChannelPool.class.getName());
    
    /** The client config. */
    private final RpcClientOptions clientConfig;
    
    /** The object factory. */
    private final PooledObjectFactory<Connection> objectFactory;
    
    /** The pool. */
    private final GenericObjectPool<Connection> pool;
    
    /**
     * Instantiates a new channel pool.
     *
     * @param rpcClient the rpc client
     * @param host the host
     * @param port the port
     */
    public ChannelPool(RpcClient rpcClient, String host, int port) {
        this.clientConfig = rpcClient.getRpcClientOptions();
        objectFactory = new ChannelPoolObjectFactory(rpcClient, host, port);
        
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setJmxEnabled(clientConfig.isJmxEnabled());
        pool = new GenericObjectPool<Connection>(objectFactory, config);
        pool.setMaxIdle(clientConfig.getMaxIdleSize());
        pool.setMaxTotal(clientConfig.getThreadPoolSize());
        pool.setMaxWaitMillis(clientConfig.getMaxWait());
        pool.setMinIdle(clientConfig.getMinIdleSize());
        pool.setMinEvictableIdleTimeMillis(clientConfig.getMinEvictableIdleTime());
        pool.setTestOnBorrow(clientConfig.isTestOnBorrow());
        pool.setTestOnReturn(clientConfig.isTestOnReturn());
        pool.setLifo(clientConfig.isLifo());
    }
    
    /**
     * Gets the channel.
     *
     * @return the channel
     */
    public Connection getChannel() {
        Connection channel = null;
        try {
            if (!clientConfig.isShortConnection()) {
                channel = pool.borrowObject();
            } else {
                channel = objectFactory.makeObject().getObject();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return channel;
    }
    
    /**
     * Return channel.
     *
     * @param channel the channel
     */
    public void returnChannel(Connection channel) {
        try {
            if (!clientConfig.isShortConnection()) {
                pool.returnObject(channel);
            } else {
                if (channel.getFuture().channel().isOpen()) {
                    channel.getFuture().channel().close();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    /**
     * Stop.
     */
    public void stop() {
        try {
            if (pool != null) {
                pool.clear();
                pool.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "stop channel failed!", e);
        }
    }
}
