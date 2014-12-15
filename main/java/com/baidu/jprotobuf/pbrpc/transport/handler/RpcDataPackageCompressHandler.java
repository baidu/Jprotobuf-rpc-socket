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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.baidu.jprotobuf.pbrpc.compress.Compress;
import com.baidu.jprotobuf.pbrpc.compress.GZipCompress;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcMeta;

/**
 * Do data compress handler
 * 
 * @author xiemalin
 * @since 1.4
 */
public class RpcDataPackageCompressHandler extends OneToOneEncoder {

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
        
        // if select compress type should do compress here
        RpcDataPackage dataPackage = (RpcDataPackage) msg;
        
        // check if do compress
        Integer compressType = dataPackage.getRpcMeta().getCompressType();
        Compress compress = null;
        if (compressType == RpcMeta.COMPERESS_GZIP) {
            compress = new GZipCompress();
        }
        
        if (compress != null) {
            byte[] data = dataPackage.getData();
            data = compress.compress(data);
            dataPackage.data(data);
        }
        
        return dataPackage;
    }

}
