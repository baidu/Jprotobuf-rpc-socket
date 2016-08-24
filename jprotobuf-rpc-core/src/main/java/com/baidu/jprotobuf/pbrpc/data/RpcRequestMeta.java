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
 * 请求包的元数据主要描述了需要调用的RPC方法信息.
 *
 * @author xiemalin
 * @see RpcMeta
 * @since 1.0
 */
public class RpcRequestMeta implements Readable, Writerable {
    

    /** default encode and decode handler. */
    private static final Codec<RpcRequestMeta> CODEC = ProtobufProxy.create(RpcRequestMeta.class);

    /** 服务名. */
    @Protobuf(required = true)
    private String serviceName;
    
    /** 方法名. */
    @Protobuf(required = true)
    private String methodName;
    
    /** 用于打印日志。可用于存放BFE_LOGID。该参数可选。. */
    @Protobuf
    private Long logId;
    
    /** 非PbRpc规范，用于传输额外的参数. */
    @Protobuf(fieldType = FieldType.BYTES)
    private byte[] extraParam;

    /**
     * Gets the serivce name.
     *
     * @return the serivce name
     */
    public String getSerivceName() {
        return serviceName;
    }

    /**
     * Sets the 服务名.
     *
     * @param serviceName the new 服务名
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Gets the 方法名.
     *
     * @return the 方法名
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the 方法名.
     *
     * @param methodName the new 方法名
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the 用于打印日志。可用于存放BFE_LOGID。该参数可选。.
     *
     * @return the 用于打印日志。可用于存放BFE_LOGID。该参数可选。
     */
    public Long getLogId() {
        return logId;
    }

    /**
     * Sets the 用于打印日志。可用于存放BFE_LOGID。该参数可选。.
     *
     * @param logId the new 用于打印日志。可用于存放BFE_LOGID。该参数可选。
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
     * copy {@link RpcRequestMeta}.
     *
     * @param meta the meta
     */
    private void copy(RpcRequestMeta meta) {
        if (meta == null) {
            return;
        }
        setLogId(meta.getLogId());
        setMethodName(meta.getMethodName());
        setServiceName(meta.getSerivceName());
        setExtraParam(meta.getExtraParam());
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

    /**
     * Gets the 非PbRpc规范，用于传输额外的参数.
     *
     * @return the 非PbRpc规范，用于传输额外的参数
     */
    public byte[] getExtraParam() {
        return extraParam;
    }

    /**
     * Sets the 非PbRpc规范，用于传输额外的参数.
     *
     * @param extraParam the new 非PbRpc规范，用于传输额外的参数
     */
    public void setExtraParam(byte[] extraParam) {
        this.extraParam = extraParam;
    }

    /**
     * Copy.
     *
     * @return the rpc request meta
     */
    public RpcRequestMeta copy() {
        RpcRequestMeta rpcRequestMeta = new RpcRequestMeta();
        rpcRequestMeta.copy(this);
        return rpcRequestMeta;
    }
    
    
}
