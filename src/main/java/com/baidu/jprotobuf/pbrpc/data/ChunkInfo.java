/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.data;

import java.io.IOException;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * 
 * Chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送
 * 
 * @author xiemalin
 * @since 1.0
 * @see RpcMeta
 */
public class ChunkInfo implements Readable, Writerable {

    private static final Codec<ChunkInfo> CODEC = ProtobufProxy.create(ChunkInfo.class);

    /**
     * 用于唯一标识一个数据流，由发送方保证其唯一性，协议不对此进行任何检查
     */
    @Protobuf(required = true)
    private Long streamId;

    /**
     * 从0开始严格递增。发送方需保证按序发送Chunk包。数据流的最后一个包chunk_id为-1。<br>
     * 由于Protobuf RPC基于TCP协议，因此包之间的顺序可以保证
     */
    @Protobuf(required = true)
    private long chunkId;

    /**
     * get the streamId
     * 
     * @return the streamId
     */
    public Long getStreamId() {
        return streamId;
    }

    /**
     * set streamId value to streamId
     * 
     * @param streamId
     *            the streamId to set
     */
    public void setStreamId(Long streamId) {
        this.streamId = streamId;
    }

    /**
     * get the chunkId
     * 
     * @return the chunkId
     */
    public long getChunkId() {
        return chunkId;
    }

    /**
     * set chunkId value to chunkId
     * 
     * @param chunkId
     *            the chunkId to set
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

}
