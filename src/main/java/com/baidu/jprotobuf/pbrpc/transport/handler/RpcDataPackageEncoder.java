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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;

/**
 * Pack client data of byte array type.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcDataPackageEncoder extends
		MessageToMessageEncoder<RpcDataPackage> {

	/**
	 * log this class
	 */
	private static final Logger LOG = Logger
			.getLogger(RpcDataPackageEncoder.class.getName());

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
		this.chunkSize = chunkSize;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcDataPackage msg,
			List<Object> out) throws Exception {

		RpcDataPackage dataPackage = (RpcDataPackage) msg;

		byte[] encodeBytes = dataPackage.write();
		if (encodeBytes != null) {
			LOG.log(Level.FINE, "Client send content byte size:"
					+ encodeBytes.length);
		}

		ByteBuf encodedMessage = Unpooled.copiedBuffer(encodeBytes);

		if (chunkSize < 0) {
			out.add(encodedMessage);
			return ;
		}

		List<RpcDataPackage> list = dataPackage.chunk(chunkSize);
		for (RpcDataPackage rpcDataPackage : list) {
			encodeBytes = rpcDataPackage.write();
			encodedMessage = Unpooled.copiedBuffer(encodeBytes);
			out.add(encodedMessage);
		}
	}

}
