/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;

/**
 * Pack client data of byte array type.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcDataPackageEncoder extends OneToOneEncoder {
    
    /**
     * log this class
     */
    private static final Logger LOG = Logger.getLogger(RpcDataPackageEncoder.class.getName());
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.netty.handler.codec.oneone.OneToOneEncoder#encode(org.jboss
     * .netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel,
     * java.lang.Object)
     */
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof RpcDataPackage)) {
            return msg;
        }
        
        RpcDataPackage dataPackage = (RpcDataPackage) msg;
        
        byte[] encodeBytes = dataPackage.write();
        if (encodeBytes !=  null) {
            LOG.log(Level.FINE, "Client send content byte size:" + encodeBytes.length);
        }
           
        return ChannelBuffers.copiedBuffer(
                ctx.getChannel().getConfig().getBufferFactory().getDefaultOrder(), encodeBytes);
    }

}
