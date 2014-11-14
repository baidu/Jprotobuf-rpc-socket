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
 * 响应包的元数据是对返回结果的描述。如果出现任何异常，错误也会放在元数据中。
 *
 * @author xiemalin
 * @since 1.0
 * @see RpcMeta
 */
public class RpcResponseMeta implements Readable, Writerable {
    
    /**
     * Decode and encode hanlder
     */
    private static final Codec<RpcResponseMeta> CODEC = ProtobufProxy.create(RpcResponseMeta.class);

    /**
     * default constrctor
     */
    public RpcResponseMeta() {
    }
    
    /**
     * constructor with errorCode and errorText
     * @param errorCode
     * @param errorText
     */
    public RpcResponseMeta(Integer errorCode, String errorText) {
        super();
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    /**
     * 发生错误时的错误号，0表示正常，非0表示错误。具体含义由应用方自行定义。
     */
    @Protobuf
    private Integer errorCode;
    
    /**
     * 错误的文本描述
     */
    @Protobuf
    private String errorText;

    /**
     * get the errorCode
     * @return the errorCode
     */
    public Integer getErrorCode() {
        if (errorCode == null) {
            errorCode = 0;
        }
        return errorCode;
    }

    /**
     * set errorCode value to errorCode
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * get the errorText
     * @return the errorText
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * set errorText value to errorText
     * @param errorText the errorText to set
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#write()
     */
    public byte[] write() {
        try {
            return CODEC.encode(this);
        } catch (IOException e) {
            throw  new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * copy {@link RpcResponseMeta}
     * @param meta
     */
    private void copy(RpcResponseMeta meta) {
        if (meta == null) {
            return;
        }
        setErrorCode(meta.getErrorCode());
        setErrorText(meta.getErrorText());
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Readable#read(byte[])
     */
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("param 'bytes' is null.");
        }
        
        try {
            RpcResponseMeta meta = CODEC.decode(bytes);
            copy(meta);
        } catch (IOException e) {
            throw  new RuntimeException(e.getMessage(), e);
        }
    }
    
    
}
