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
 * 请求包的元数据主要描述了需要调用的RPC方法信息
 *
 * @author xiemalin
 * @since 1.0
 * @see RpcMeta
 */
public class RpcRequestMeta implements Readable, Writerable {
    

    /**
     * default encode and decode handler
     */
    private static final Codec<RpcRequestMeta> CODEC = ProtobufProxy.create(RpcRequestMeta.class);

    /**
     * 服务名
     */
    @Protobuf(required = true)
    private String serviceName;
    
    /**
     * 方法名
     */
    @Protobuf(required = true)
    private String methodName;
    
    /**
     * 用于打印日志。可用于存放BFE_LOGID。该参数可选。
     */
    @Protobuf
    private Long logId;

    /**
     * get the serivceName
     * @return the serivceName
     */
    public String getSerivceName() {
        return serviceName;
    }

    /**
     * set serivceName value to serivceName
     * @param serviceName the serivceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * get the methodName
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * set methodName value to methodName
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * get the logId
     * @return the logId
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * set logId value to logId
     * @param logId the logId to set
     */
    public void setLogId(Long logId) {
        this.logId = logId;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#write(byte[])
     */
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("param 'bytes' is null.");
        }
        
        try {
            RpcRequestMeta meta = CODEC.decode(bytes);
            copy(meta);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * copy {@link RpcRequestMeta}
     * 
     * @param meta
     */
    private void copy(RpcRequestMeta meta) {
        if (meta == null) {
            return;
        }
        setLogId(meta.getLogId());
        setMethodName(meta.getMethodName());
        setServiceName(meta.getSerivceName());
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Readable#read()
     */
    public byte[] write() {
        try {
            return CODEC.encode(this);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    
}
