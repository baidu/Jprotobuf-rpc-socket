/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport.handler;

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
        
        // TODO if select compress type should do compress here
        RpcDataPackage dataPackage = (RpcDataPackage) msg;
        
        byte[] encodeBytes = dataPackage.write();

        return ChannelBuffers.copiedBuffer(
                ctx.getChannel().getConfig().getBufferFactory().getDefaultOrder(), encodeBytes);
    }

}
