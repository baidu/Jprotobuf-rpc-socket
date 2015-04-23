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
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;


/**
 * Pool Object Factory for netty channel
 * 
 * @author sunzhongyi, xuyuepeng
 * @author xiemalin
 */
public class ChannelPoolObjectFactory implements PooledObjectFactory<Connection> {
    private static final Logger LOGGER = Logger.getLogger(ChannelPoolObjectFactory.class.getName());
    private final RpcClient rpcClient;
    private final String host;
    private final int port;

    public ChannelPoolObjectFactory(RpcClient rpcClient, String host, int port) {
        this.rpcClient = rpcClient;
        this.host = host;
        this.port = port;
    }

    public Connection fetchConnection() {
    	return new Connection(rpcClient);
    }

    public PooledObject<Connection> makeObject() throws Exception {
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
            LOGGER.log(Level.SEVERE, "failed to get result from stp", future.cause());
        } else {
            connection.setIsConnected(true);
        }

        future.addListener(new RpcChannelFutureListener(connection));
        connection.setFuture(future);
        
        return new DefaultPooledObject<Connection>(connection);
    }

	public void destroyObject(PooledObject<Connection> p) throws Exception {
		Connection c = p.getObject();
		Channel channel = c.getFuture().channel();
		if (channel.isOpen() && channel.isActive()) {
			channel.close();
		}
	}

	public boolean validateObject(PooledObject<Connection> p) {
		Connection c = p.getObject();
        Channel channel = c.getFuture().channel();
        return channel.isOpen() && channel.isActive();
	}
    
	public void activateObject(PooledObject<Connection> p) throws Exception {
		 // nothing to do
		
	}

	public void passivateObject(PooledObject<Connection> p) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}
