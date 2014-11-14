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
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * RPC meta data
 *
 * @author xiemalin
 * @since 1.0
 * @see RpcRequestMeta
 * @see RpcResponseMeta
 */
public class RpcMeta implements Readable, Writerable {
    
    public static final int COMPRESS_NO = 0;
    public static final int COMPRESS_SNAPPY = 1;
    public static final int COMPERESS_GZIP = 2;
    
    /**
     * Decode and encode handler
     */
    private static final Codec<RpcMeta> CODEC = ProtobufProxy.create(RpcMeta.class);

    /**
     * 请求包元数据
     */
    @Protobuf(fieldType = FieldType.OBJECT)
    private RpcRequestMeta request;
    
    /**
     * 响应包元数据
     */
    @Protobuf(fieldType = FieldType.OBJECT)
    private RpcResponseMeta response;
    
    /**
     * 0 不压缩
     * 1 使用Snappy 1.0.5
     * 2 使用gzip
     */
    @Protobuf
    private Integer compressType;
    
    /**
     * 请求包中的该域由请求方设置，用于唯一标识一个RPC请求。<br>
     * 请求方有义务保证其唯一性，协议本身对此不做任何检查。<br>
     * 响应方需要在对应的响应包里面将correlation_id设为同样的值。
     */
    @Protobuf
    private Long correlationId;
    
    /**
     * 附件大小
     */
    @Protobuf
    private Integer attachmentSize;
    
    /**
     * Chunk模式本质上是将一个大的数据流拆分成一个个小的Chunk包按序进行发送。如何拆分还原由通信双方确定
     */
    @Protobuf
    private ChunkInfo chuckInfo;
    
    /**
     * 用于存放身份认证相关信息
     */
    @Protobuf(fieldType = FieldType.BYTES)
    private byte[] authenticationData;

    /**
     * get the request
     * @return the request
     */
    public RpcRequestMeta getRequest() {
        return request;
    }

    /**
     * set request value to request
     * @param request the request to set
     */
    public void setRequest(RpcRequestMeta request) {
        this.request = request;
    }

    /**
     * get the response
     * @return the response
     */
    public RpcResponseMeta getResponse() {
        return response;
    }

    /**
     * set response value to response
     * @param response the response to set
     */
    public void setResponse(RpcResponseMeta response) {
        this.response = response;
    }

    /**
     * get the compressType
     * @return the compressType
     */
    public Integer getCompressType() {
        if (compressType == null) {
            compressType = 0;
        }
        return compressType;
    }

    /**
     * set compressType value to compressType
     * @param compressType the compressType to set
     */
    public void setCompressType(Integer compressType) {
        this.compressType = compressType;
    }

    /**
     * get the correlationId
     * @return the correlationId
     */
    public Long getCorrelationId() {
        if (correlationId == null) {
            correlationId = 0L;
        }
        return correlationId;
    }

    /**
     * set correlationId value to correlationId
     * @param correlationId the correlationId to set
     */
    public void setCorrelationId(Long correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * get the attachmentSize
     * @return the attachmentSize
     */
    public Integer getAttachmentSize() {
        if (attachmentSize == null) {
            return 0;
        }
        return attachmentSize;
    }

    /**
     * set attachmentSize value to attachmentSize
     * @param attachmentSize the attachmentSize to set
     */
    public void setAttachmentSize(Integer attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    /**
     * get the chuckInfo
     * @return the chuckInfo
     */
    public ChunkInfo getChuckInfo() {
        return chuckInfo;
    }

    /**
     * set chuckInfo value to chuckInfo
     * @param chuckInfo the chuckInfo to set
     */
    public void setChuckInfo(ChunkInfo chuckInfo) {
        this.chuckInfo = chuckInfo;
    }

    /**
     * get the authenticationData
     * @return the authenticationData
     */
    public byte[] getAuthenticationData() {
        return authenticationData;
    }

    /**
     * set authenticationData value to authenticationData
     * @param authenticationData the authenticationData to set
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
            copy(meta);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * copy {@link RpcMeta}
     * 
     * @param meta
     */
    private void copy(RpcMeta meta) {
        if (meta == null) {
            return;
        }
        setRequest(meta.getRequest());
        setResponse(meta.getResponse());
        setAttachmentSize(meta.getAttachmentSize());
        setAuthenticationData(meta.getAuthenticationData());
        setCompressType(meta.getCompressType());
        setCorrelationId(meta.getCorrelationId());
        setChuckInfo(meta.getChuckInfo());
    }
    
}
