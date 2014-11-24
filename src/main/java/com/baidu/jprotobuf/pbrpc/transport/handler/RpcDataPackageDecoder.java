/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcHeadMeta;

/**
 * Decode RpcDataPackage from received bytes
 * 
 * @author xiemalin
 * @since 1.0
 * @see RpcDataPackage
 */
public class RpcDataPackageDecoder extends FrameDecoder {

    private static Logger LOG = Logger.getLogger(RpcDataPackageDecoder.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.netty.handler.codec.frame.FrameDecoder#decode(org.jboss.netty
     * .channel.ChannelHandlerContext, org.jboss.netty.channel.Channel,
     * org.jboss.netty.buffer.ChannelBuffer)
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        // Make sure if the length field was received.
        if (buf.readableBytes() < RpcHeadMeta.SIZE) {
            // The length field was not received yet - return null.
            // This method will be invoked again when more packets are
            // received and appended to the buffer.
            return null;
        }

        // The length field is in the buffer.

        // Mark the current buffer position before reading the length field
        // because the whole frame might not be in the buffer yet.
        // We will reset the buffer position to the marked position if
        // there's not enough bytes in the buffer.
        buf.markReaderIndex();

        // Read the RPC head
        long rpcMessageDecoderStart = System.nanoTime();
        ByteBuffer buffer = buf.toByteBuffer(buf.readerIndex(), RpcHeadMeta.SIZE);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = new byte[RpcHeadMeta.SIZE];
        buffer.get(bytes);

        RpcHeadMeta headMeta = new RpcHeadMeta();
        headMeta.read(bytes);

        // get total message size
        int messageSize = headMeta.getMessageSize();

        // Make sure if there's enough bytes in the buffer.
        if (buf.readableBytes() < messageSize) {
            // The whole bytes were not received yet - return null.
            // This method will be invoked again when more packets are
            // received and appended to the buffer.

            // Reset to the marked position to read the length field again
            // next time.
            buf.resetReaderIndex();

            return null;
        }

        // check magic code
        String magicCode = headMeta.getMagicCodeAsString();
        if (!ProtocolConstant.MAGIC_CODE.equals(magicCode)) {
            throw new Exception("Error magic code:" + magicCode);
        }

        // There's enough bytes in the buffer. Read it.
        byte[] totalBytes = new byte[messageSize];
        buf.readBytes(totalBytes, 0, messageSize);

        RpcDataPackage rpcDataPackage = new RpcDataPackage();
        
        rpcDataPackage.read(totalBytes);

        long rpcMessageDecoderEnd = System.nanoTime();
        LOG.log(Level.FINE, "[profiling] nshead decode cost : " + (rpcMessageDecoderEnd - rpcMessageDecoderStart)
                / 1000);

        return rpcDataPackage;
    }

}
