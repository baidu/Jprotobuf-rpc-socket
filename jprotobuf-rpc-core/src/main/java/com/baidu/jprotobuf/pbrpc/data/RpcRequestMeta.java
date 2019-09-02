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

package com.baidu.jprotobuf.pbrpc.data;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Protobuf(required = true, order = 1)
    private String serviceName;

    /** 方法名. */
    @Protobuf(required = true, order = 2)
    private String methodName;

    /** 用于打印日志。可用于存放BFE_LOGID。该参数可选。. */
    @Protobuf(order = 3)
    private Long logId;

    /** 分布式追踪 Trace ID. */
    @Protobuf(order = 4)
    private Long traceId;

    /** 分布式追踪 Span ID. */
    @Protobuf(order = 5)
    private Long spanId;

    /** 分布式追踪 Parent Span ID. */
    @Protobuf(order = 6)
    private Long parentSpanId;

    /** 扩展字段. */
    @Protobuf(order = 7)
    private List<RpcRequestMetaExtField> extFields;

    /** 非PbRpc规范，用于传输额外的参数. */
    @Protobuf(fieldType = FieldType.BYTES, order = 110)
    private byte[] extraParam;

    /** 非PbRpc规范，用于传输trace 字符串， 可以用于补充traceId. */
    @Protobuf(order = 111)
    private String traceKey;

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    public String getServiceName() {
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

    /**
     * Read.
     *
     * @param bytes the bytes
     */
    /*
     * (non-Javadoc)
     * 
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
        setServiceName(meta.getServiceName());
        setExtraParam(meta.getExtraParam());
        setTraceId(meta.getTraceId());
        setSpanId(meta.getSpanId());
        setParentSpanId(meta.getParentSpanId());
        setExtFields(meta.getExtFields());
        setTraceKey(meta.getTraceKey());
    }

    /**
     * Write.
     *
     * @return the byte[]
     */
    /*
     * (non-Javadoc)
     * 
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

    /**
     * Gets the ext fields.
     *
     * @return the ext fields
     */
    public List<RpcRequestMetaExtField> getExtFields() {
        return extFields;
    }

    /**
     * Gets the ext fields as map.
     *
     * @return the ext fields as map
     */
    public Map<String, String> getExtFieldsAsMap() {
        if (extFields == null) {
            return Collections.emptyMap();
        }

        Map<String, String> ret = new HashMap<String, String>();
        for (RpcRequestMetaExtField rpcRequestMetaExtField : extFields) {
            ret.put(rpcRequestMetaExtField.getKey(), rpcRequestMetaExtField.getValue());
        }
        return ret;
    }

    /**
     * Sets the ext fields.
     *
     * @param extFields the new ext fields
     */
    public void setExtFields(List<RpcRequestMetaExtField> extFields) {
        this.extFields = extFields;
    }

    /**
     * 获取分布式追踪 Trace ID.
     *
     * @return Trace ID
     */
    public Long getTraceId() {
        return traceId;
    }

    /**
     * 设置分布式追踪 Trace ID.
     *
     * @param traceId Trace ID
     */
    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    /**
     * 获取分布式追踪 Trace ID.
     *
     * @return Trace ID
     */
    public Long getSpanId() {
        return spanId;
    }

    /**
     * 设置分布式追踪 Span ID.
     *
     * @param spanId Span ID
     */
    public void setSpanId(Long spanId) {
        this.spanId = spanId;
    }

    /**
     * 获取分布式追踪 Parent Span ID.
     *
     * @return Parent Span ID
     */
    public Long getParentSpanId() {
        return parentSpanId;
    }

    /**
     * 设置分布式追踪 Parent Span ID.
     *
     * @param parentSpanId Parent Span ID
     */
    public void setParentSpanId(Long parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    /**
     * Gets the trace key.
     *
     * @return the trace key
     */
    public String getTraceKey() {
        return traceKey;
    }

    /**
     * Sets the trace key.
     *
     * @param traceKey the new trace key
     */
    public void setTraceKey(String traceKey) {
        this.traceKey = traceKey;
    }

}
