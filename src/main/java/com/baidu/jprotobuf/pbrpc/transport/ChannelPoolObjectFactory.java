package com.baidu.jprotobuf.pbrpc.transport;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool.PoolableObjectFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
/**
 * Pool Object Factory for netty channel
 * 
 * @author sunzhongyi, xuyuepeng
 * 
 */
public class ChannelPoolObjectFactory implements PoolableObjectFactory {
    private static final Logger LOGGER = Logger.getLogger(ChannelPoolObjectFactory.class.getName());
    private final RpcClient rpcClient;
    private final String host;
    private final int port;
    
    public ChannelPoolObjectFactory(RpcClient rpcClient, String host, int port) {
        super();
        this.rpcClient = rpcClient;
        this.host = host;
        this.port = port;
    }
    
    public Connection fetchConnection() {
        Connection connection = new Connection(rpcClient);
        return connection;
    }
    
    
    public Object makeObject() throws Exception {
        Connection connection = fetchConnection();
        
        InetSocketAddress address;
        if (host == null) {
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(host, port);
        }
        
        ChannelFuture future = this.rpcClient.connect(address);
        
        // Wait until the connection is made successfully.
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            LOGGER.log(Level.SEVERE, "failed to get result from stp", connection.getFuture().getCause());
        } else {
            connection.setIsConnected(true);
        }
        
        future.addListener(new RpcChannelFutureListener(connection));
        connection.setFuture(future);

        return connection;
    }
    
    public void destroyObject(Object object) throws Exception {
        Connection c = (Connection) object;
        Channel channel = c.getFuture().getChannel();
        if (channel.isOpen() && channel.isConnected()) {
            channel.close();
        }
    }
    
    public boolean validateObject(Object object) {
        Connection c = (Connection) object;
        Channel channel = c.getFuture().getChannel();
        return channel.isOpen() && channel.isConnected();
    }
    
    public void activateObject(Object channel) throws Exception {
        // nothing to do
    }
    
    public void passivateObject(Object channel) throws Exception {
        // nothing to do
    }
    
}
