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
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * RPC meta data.
 *
 * @author xiemalin
 * @see RpcRequestMeta
 * @see RpcResponseMeta
 * @since 1.0
 */
public class RpcMeta implements Readable, Writerable, Cloneable {
    
    /** The Constant COMPRESS_NO. */
    public static final int COMPRESS_NO = 0;
    
    /** The Constant COMPRESS_SNAPPY. */
    public static final int COMPRESS_SNAPPY = 1;
    
    /** The Constant COMPERESS_GZIP. */
    public static final int COMPERESS_GZIP = 2;
    
    /** Decode and encode handler. */
    private static final Codec<RpcMeta> CODEC = ProtobufProxy.create(RpcMeta.class);

    /** 请求包元数据. */
    @Protobuf(fieldType = FieldType.OBJECT)
    private RpcRequestMeta request;
    
    /** 响应包元数据. */
    @Protobuf(fieldType = FieldType.OBJECT)
    private RpcResponseMeta response;
    
    /**
     * 0 不压缩
     * 1 使用Snappy 1.0.5
     * 2 使用gzip
     */
    @Protobuf
    private Integer compressType;
    
    /** 请求包中的该域由请求方设置，用于唯一标识一个RPC请求。<br> 请求方有义务保证其唯一性，协议本身对此不做任何检查。<br> 响应方需要在对应的响应包里面将correlation_id设为同样的值。. */
    @Protobuf
    private Long correlationId;
    
    /** 附件大小. */
    @Protobuf
    private Integer attachmentSize;
    
    /** Chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送。如何拆分还原由通信双方确定. */
    @Protobuf
    private ChunkInfo chunkInfo;
    
    /** 用于存放身份认证相关信息. */
    @Protobuf(fieldType = FieldType.BYTES)
    private byte[] authenticationData;

    /**
     * Gets the 请求包元数据.
     *
     * @return the 请求包元数据
     */
    public RpcRequestMeta getRequest() {
        return request;
    }

    /**
     * Sets the 请求包元数据.
     *
     * @param request the new 请求包元数据
     */
    public void setRequest(RpcRequestMeta request) {
        this.request = request;
    }

    /**
     * Gets the 响应包元数据.
     *
     * @return the 响应包元数据
     */
    public RpcResponseMeta getResponse() {
        return response;
    }

    /**
     * Sets the 响应包元数据.
     *
     * @param response the new 响应包元数据
     */
    public void setResponse(RpcResponseMeta response) {
        this.response = response;
    }

    /**
     * Gets the 0 不压缩 1 使用Snappy 1.
     *
     * @return the 0 不压缩 1 使用Snappy 1
     */
    public Integer getCompressType() {
        if (compressType == null) {
            compressType = 0;
        }
        return compressType;
    }

    /**
     * Sets the 0 不压缩 1 使用Snappy 1.
     *
     * @param compressType the new 0 不压缩 1 使用Snappy 1
     */
    public void setCompressType(Integer compressType) {
        this.compressType = compressType;
    }

    /**
     * Gets the 请求包中的该域由请求方设置，用于唯一标识一个RPC请求。<br> 请求方有义务保证其唯一性，协议本身对此不做任何检查。<br> 响应方需要在对应的响应包里面将correlation_id设为同样的值。.
     *
     * @return the 请求包中的该域由请求方设置，用于唯一标识一个RPC请求。<br> 请求方有义务保证其唯一性，协议本身对此不做任何检查。<br> 响应方需要在对应的响应包里面将correlation_id设为同样的值。
     */
    public Long getCorrelationId() {
        if (correlationId == null) {
            correlationId = 0L;
        }
        return correlationId;
    }

    /**
     * Sets the 请求包中的该域由请求方设置，用于唯一标识一个RPC请求。<br> 请求方有义务保证其唯一性，协议本身对此不做任何检查。<br> 响应方需要在对应的响应包里面将correlation_id设为同样的值。.
     *
     * @param correlationId the new 请求包中的该域由请求方设置，用于唯一标识一个RPC请求。<br> 请求方有义务保证其唯一性，协议本身对此不做任何检查。<br> 响应方需要在对应的响应包里面将correlation_id设为同样的值。
     */
    public void setCorrelationId(Long correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets the 附件大小.
     *
     * @return the 附件大小
     */
    public Integer getAttachmentSize() {
        if (attachmentSize == null) {
            return 0;
        }
        return attachmentSize;
    }

    /**
     * Sets the 附件大小.
     *
     * @param attachmentSize the new 附件大小
     */
    public void setAttachmentSize(Integer attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    /**
     * Gets the 用于存放身份认证相关信息.
     *
     * @return the 用于存放身份认证相关信息
     */
    public byte[] getAuthenticationData() {
        return authenticationData;
    }

    /**
     * Sets the 用于存放身份认证相关信息.
     *
     * @param authenticationData the new 用于存放身份认证相关信息
     */
    public void setAuthenticationData(byte[] authenticationData) {
        this.authenticationData = authenticationData;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#write()
     */
    public byte[] write() {
        try {
            return CODEC.encode(this);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Readable#read(byte[])
     */
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("param 'bytes' is null.");
        }
        try {
            RpcMeta meta = CODEC.decode(bytes);
            copyReference(meta);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * copy {@link RpcMeta}.
     *
     * @param meta the meta
     */
    private void copyReference(RpcMeta meta) {
        if (meta == null) {
            return;
        }
        setRequest(meta.getRequest());
        setResponse(meta.getResponse());
        setAttachmentSize(meta.getAttachmentSize());
        setAuthenticationData(meta.getAuthenticationData());
        setCompressType(meta.getCompressType());
        setCorrelationId(meta.getCorrelationId());
        setChunkInfo(meta.getChunkInfo());
    }
    
    /**
     * Copy.
     *
     * @return the rpc meta
     */
    public RpcMeta copy() {
        RpcMeta rpcMeta = new RpcMeta();
        
        if (chunkInfo != null) {
            rpcMeta.setChunkInfo(chunkInfo.copy());
        }
        if (request != null) {
            rpcMeta.setRequest(request.copy());
        }
        if (response != null) {
            rpcMeta.setResponse(response.copy());
        }
        rpcMeta.setAttachmentSize(attachmentSize);
        rpcMeta.setAuthenticationData(authenticationData);
        rpcMeta.setCompressType(compressType);
        rpcMeta.setCorrelationId(correlationId);
        
        
        return rpcMeta;
    }

    /**
     * Gets the chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送。如何拆分还原由通信双方确定.
     *
     * @return the chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送。如何拆分还原由通信双方确定
     */
    public ChunkInfo getChunkInfo() {
        return chunkInfo;
    }

    /**
     * Sets the chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送。如何拆分还原由通信双方确定.
     *
     * @param chunkInfo the new chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送。如何拆分还原由通信双方确定
     */
    public void setChunkInfo(ChunkInfo chunkInfo) {
        this.chunkInfo = chunkInfo;
    }
    
}
