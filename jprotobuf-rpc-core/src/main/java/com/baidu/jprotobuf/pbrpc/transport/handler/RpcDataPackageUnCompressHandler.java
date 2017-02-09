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

package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.util.List;

import com.baidu.jprotobuf.pbrpc.compress.Compress;
import com.baidu.jprotobuf.pbrpc.compress.GZipCompress;
import com.baidu.jprotobuf.pbrpc.compress.SnappyCompress;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcMeta;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Do data compress handler.
 *
 * @author xiemalin
 * @since 1.4
 */
@Sharable
public class RpcDataPackageUnCompressHandler extends MessageToMessageDecoder<RpcDataPackage> {
    
    /*
     * (non-Javadoc)
     * 
     * @see io.netty.handler.codec.MessageToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext,
     * java.lang.Object, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, RpcDataPackage msg, List<Object> out) throws Exception {

        // if select compress type should do compress here
        RpcDataPackage dataPackage = (RpcDataPackage) msg;

        try {
            // check if do compress
            Integer compressType = dataPackage.getRpcMeta().getCompressType();
            Compress compress = null;
            if (compressType == RpcMeta.COMPERESS_GZIP) {
                compress = new GZipCompress();
            } else if (compressType == RpcMeta.COMPRESS_SNAPPY) {
                compress = new SnappyCompress();
            }

            if (compress != null) {
                byte[] data = dataPackage.getData();
                data = compress.unCompress(data);
                dataPackage.data(data);
            }
        } catch (Exception e) {
            dataPackage.errorCode(ErrorCodes.ST_ERROR_COMPRESS);
            dataPackage.errorText("Data uncompress failed due to " + e.getMessage());
        }
        out.add(dataPackage);

    }

}
