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

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import java.io.IOException;

/**
 * {@link RpcRequestMeta} 扩展字段.
 */
public class RpcRequestMetaExtField implements Readable, Writerable {
    /**
     * default encode and decode handler.
     */
    private static final Codec<RpcRequestMetaExtField> CODEC = ProtobufProxy.create(RpcRequestMetaExtField.class);

    /** 字段 Key. */
    @Protobuf(required = true, order = 1)
    private String key;

    /** 字段 Value. */
    @Protobuf(required = true, order = 2)
    private String value;

    /**
     * 
     */
    public RpcRequestMetaExtField() {
        super();
    }

    /**
     * @param key
     * @param value
     */
    public RpcRequestMetaExtField(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * 获取字段 Key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置字段 key.
     *
     * @param key the new key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取字段 Value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置字段 Value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Read.
     *
     * @param bytes the bytes
     */
    @Override
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("param 'bytes' is null.");
        }
        try {
            RpcRequestMetaExtField extField = CODEC.decode(bytes);
            copy(extField);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Write.
     *
     * @return the byte[]
     */
    @Override
    public byte[] write() {
        try {
            return CODEC.encode(this);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Copy.
     *
     * @param extField the ext field
     */
    private void copy(RpcRequestMetaExtField extField) {
        if (extField == null) {
            return;
        }
        this.key = extField.key;
        this.value = extField.value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RpcRequestMetaExtField other = (RpcRequestMetaExtField) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
