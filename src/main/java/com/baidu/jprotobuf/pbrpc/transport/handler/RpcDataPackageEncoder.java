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

package com.baidu.jprotobuf.pbrpc.transport.handler;

import static org.jboss.netty.channel.Channels.write;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
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

    private long chunkSize = -1;

    /**
     * get the chunkSize
     * 
     * @return the chunkSize
     */
    public long getChunkSize() {
        return chunkSize;
    }

    /**
     * set chunkSize value to chunkSize
     * 
     * @param chunkSize
     *            the chunkSize to set
     */
    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * 
     */
    public RpcDataPackageEncoder() {
    }

    /**
     * @param chunkSize
     */
    public RpcDataPackageEncoder(long chunkSize) {
        super();
        this.chunkSize = chunkSize;
    }

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
        if (encodeBytes != null) {
            LOG.log(Level.FINE, "Client send content byte size:" + encodeBytes.length);
        }

        ChannelBuffer encodedMessage = ChannelBuffers.copiedBuffer(ctx.getChannel().getConfig().getBufferFactory()
                .getDefaultOrder(), encodeBytes);

        if (chunkSize < 0) {
            return encodedMessage;
        }

        List<RpcDataPackage> list = dataPackage.chunk(chunkSize);
        for (RpcDataPackage rpcDataPackage : list) {
            encodeBytes = rpcDataPackage.write();
            encodedMessage = ChannelBuffers.copiedBuffer(ctx.getChannel().getConfig().getBufferFactory()
                    .getDefaultOrder(), encodeBytes);
            write(channel, encodedMessage);
        }

        return null;
    }

}
