package com.baidu.jprotobuf.pbrpc.transport;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Adapter for netty channel. Used by Mcpack Netty Client {@link NettyClient}.
 * 
 * @author xuyuepeng, sunzhongyi, lijianbin
 * 
 */
public class ChannelPool {
    private static final Logger LOGGER = Logger.getLogger(ChannelPool.class.getName());
    
    private final RpcClientOptions clientConfig;
    
    private final PoolableObjectFactory objectFactory;
    private final GenericObjectPool pool;
    
    public ChannelPool(RpcClient rpcClient, String host, int port) {
        super();
        this.clientConfig = rpcClient.getRpcClientOptions();
        objectFactory = new ChannelPoolObjectFactory(rpcClient, host, port);
        pool = new GenericObjectPool(objectFactory);
        pool.setMaxIdle(clientConfig.getThreadPoolSize());
        pool.setMaxActive(clientConfig.getThreadPoolSize());
        pool.setMaxWait(clientConfig.getMaxWait());
        pool.setMinEvictableIdleTimeMillis(clientConfig.getMinEvictableIdleTime());
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(true);
    }
    
    public Connection getChannel() {
        Connection channel = null;
        try {
            if (!clientConfig.isShortConnection()) {
                channel = (Connection) pool.borrowObject();
            } else {
                channel = (Connection) objectFactory.makeObject();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return channel;
    }
    
    public void returnChannel(Connection channel) {
        try {
            if (!clientConfig.isShortConnection()) {
                pool.returnObject(channel);
            } else {
                if (channel.getFuture().getChannel().isOpen()) {
                    channel.getFuture().getChannel().close();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    public void stop() {
        try {
            if (pool != null) {
                pool.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "stop channel failed!", e);
        }
    }
}
