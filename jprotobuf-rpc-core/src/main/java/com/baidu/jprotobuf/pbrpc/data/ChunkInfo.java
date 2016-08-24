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

package com.baidu.jprotobuf.pbrpc.data;

import java.io.IOException;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * Chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送.
 *
 * @author xiemalin
 * @see RpcMeta
 * @since 1.0
 */
public class ChunkInfo implements Readable, Writerable {

    /** The Constant CODEC. */
    private static final Codec<ChunkInfo> CODEC = ProtobufProxy.create(ChunkInfo.class);

    /** 用于唯一标识一个数据流，由发送方保证其唯一性，协议不对此进行任何检查. */
    @Protobuf(required = true)
    private Long streamId;

    /** 从0开始严格递增。发送方需保证按序发送Chunk包。数据流的最后一个包chunk_id为-1。<br> 由于Protobuf RPC基于TCP协议，因此包之间的顺序可以保证. */
    @Protobuf(required = true)
    private long chunkId = -1;

    /**
     * Gets the 用于唯一标识一个数据流，由发送方保证其唯一性，协议不对此进行任何检查.
     *
     * @return the 用于唯一标识一个数据流，由发送方保证其唯一性，协议不对此进行任何检查
     */
    public Long getStreamId() {
        return streamId;
    }

    /**
     * Sets the 用于唯一标识一个数据流，由发送方保证其唯一性，协议不对此进行任何检查.
     *
     * @param streamId the new 用于唯一标识一个数据流，由发送方保证其唯一性，协议不对此进行任何检查
     */
    public void setStreamId(Long streamId) {
        this.streamId = streamId;
    }

    /**
     * Gets the 从0开始严格递增。发送方需保证按序发送Chunk包。数据流的最后一个包chunk_id为-1。<br> 由于Protobuf RPC基于TCP协议，因此包之间的顺序可以保证.
     *
     * @return the 从0开始严格递增。发送方需保证按序发送Chunk包。数据流的最后一个包chunk_id为-1。<br> 由于Protobuf RPC基于TCP协议，因此包之间的顺序可以保证
     */
    public long getChunkId() {
        return chunkId;
    }

    /**
     * Sets the 从0开始严格递增。发送方需保证按序发送Chunk包。数据流的最后一个包chunk_id为-1。<br> 由于Protobuf RPC基于TCP协议，因此包之间的顺序可以保证.
     *
     * @param chunkId the new 从0开始严格递增。发送方需保证按序发送Chunk包。数据流的最后一个包chunk_id为-1。<br> 由于Protobuf RPC基于TCP协议，因此包之间的顺序可以保证
     */
    public void setChunkId(long chunkId) {
        this.chunkId = chunkId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#write()
     */
    public byte[] write() {
        try {
            return CODEC.encode(this);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.remoting.pbrpc.Readable#read(byte[])
     */
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("param 'bytes' is null");
        }
        try {
            ChunkInfo info = CODEC.decode(bytes);
            setChunkId(info.getChunkId());
            setStreamId(info.getStreamId());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Copy.
     *
     * @return the chunk info
     */
    public ChunkInfo copy() {
        ChunkInfo chunkInfo = new ChunkInfo();
        chunkInfo.setChunkId(chunkId);
        chunkInfo.setStreamId(streamId);
        return chunkInfo;
    }

}
