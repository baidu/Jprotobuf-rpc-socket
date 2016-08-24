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

import java.nio.ByteBuffer;



/**
 * Head package for PBRPC.
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcHeadMeta implements Writerable, Readable {
    
    /** RPC meta head size. */
    public static final int SIZE = 12;

    /** 协议标识. */
    private byte[] magicCode;
    
    /** message body size include. */
    private int messageSize;
    
    /** RPC meta size. */
    private int metaSize;

    /**
     * Gets the 协议标识.
     *
     * @return the 协议标识
     */
    public byte[] getMagicCode() {
        return magicCode;
    }
    
    /**
     * Gets the magic code as string.
     *
     * @return the magic code as string
     */
    public String getMagicCodeAsString() {
        if (magicCode == null) {
            return null;
        }
        return new String(magicCode, ProtocolConstant.CHARSET);
    }
    
    /**
     * Sets the 协议标识.
     *
     * @param magicCode the new 协议标识
     */
    public void setMagicCode(String magicCode) {
        if (magicCode == null) {
            throw new IllegalArgumentException("invalid magic code. size must be 4.");
        }
        setMagicCode(magicCode.getBytes(ProtocolConstant.CHARSET));
    }

    /**
     * Sets the 协议标识.
     *
     * @param magicCode the new 协议标识
     */
    public void setMagicCode(byte[] magicCode) {
        if (magicCode == null || magicCode.length != 4) {
            throw new IllegalArgumentException("invalid magic code. size must be 4.");
        }
        this.magicCode = magicCode;
    }

    /**
     * Gets the message body size include.
     *
     * @return the message body size include
     */
    public int getMessageSize() {
        return messageSize;
    }

    /**
     * Sets the message body size include.
     *
     * @param messageSize the new message body size include
     */
    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    /**
     * Gets the rPC meta size.
     *
     * @return the rPC meta size
     */
    public int getMetaSize() {
        return metaSize;
    }

    /**
     * Sets the rPC meta size.
     *
     * @param metaSize the new rPC meta size
     */
    public void setMetaSize(int metaSize) {
        this.metaSize = metaSize;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#read()
     */
    public byte[] write() {
        ByteBuffer allocate = ByteBuffer.allocate(SIZE);
        allocate.put(magicCode);
        allocate.putInt(messageSize);
        allocate.putInt(metaSize);
        byte[] ret = allocate.array();
        allocate.clear();
        return ret;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#write(byte[])
     */
    public void read(byte[] bytes) {
        if (bytes == null || bytes.length != SIZE) {
            throw new IllegalArgumentException("invalid byte array. size must be " + SIZE);
        }
        
        ByteBuffer allocate = ByteBuffer.wrap(bytes);
        magicCode = new byte[4]; // magic code size must be 4.
        allocate.get(magicCode);
        messageSize = allocate.getInt();
        metaSize = allocate.getInt();
        
        allocate.clear();
    }
    
    /**
     * Copy.
     *
     * @return the rpc head meta
     */
    public RpcHeadMeta copy() {
        RpcHeadMeta rpcHeadMeta = new RpcHeadMeta();
        rpcHeadMeta.setMagicCode(getMagicCode());
        rpcHeadMeta.setMessageSize(getMessageSize());
        rpcHeadMeta.setMetaSize(getMetaSize());
        return rpcHeadMeta;
    }

}
