/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
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
    
    /**
     * RPC meta head size
     */
    public static final int SIZE = 12;

    /**
     * 协议标识 
     */
    private byte[] magicCode;
    
    /**
     * total size
     */
    private int messageSize;
    
    /**
     * RPC meta size
     */
    private int metaSize;

    /**
     * get the magicCode
     * @return the magicCode
     */
    public byte[] getMagicCode() {
        return magicCode;
    }
    
    /**
     * @return the magic code of string
     */
    public String getMagicCodeAsString() {
        if (magicCode == null) {
            return null;
        }
        return new String(magicCode, ProtocolConstant.CHARSET);
    }
    
    /**
     * set magicCode value to magicCode
     * @param magicCode magic code string
     */
    public void setMagicCode(String magicCode) {
        if (magicCode == null) {
            throw new IllegalArgumentException("invalid magic code. size must be 4.");
        }
        setMagicCode(magicCode.getBytes(ProtocolConstant.CHARSET));
    }

    /**
     * set magicCode value to magicCode
     * @param magicCode the magicCode to set
     */
    public void setMagicCode(byte[] magicCode) {
        if (magicCode == null || magicCode.length != 4) {
            throw new IllegalArgumentException("invalid magic code. size must be 4.");
        }
        this.magicCode = magicCode;
    }

    /**
     * get the messageSize
     * @return the messageSize
     */
    public int getMessageSize() {
        return messageSize;
    }

    /**
     * set messageSize value to messageSize
     * @param messageSize the messageSize to set
     */
    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    /**
     * get the metaSize
     * @return the metaSize
     */
    public int getMetaSize() {
        return metaSize;
    }

    /**
     * set metaSize value to metaSize
     * @param metaSize the metaSize to set
     */
    public void setMetaSize(int metaSize) {
        this.metaSize = metaSize;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.remoting.pbrpc.Writerable#read()
     */
    public byte[] write() {
/*        
        byte[] ret = new byte[SIZE];
        System.arraycopy(magicCode, 0, ret, 0, 4);
        System.arraycopy(messageSize, 0, ret, 0, 4);*/
        
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
        magicCode = new byte[4];
        for (int i = 0; i < 4; i++) {
            magicCode[i] = allocate.get();
        }
        messageSize = allocate.getInt();
        metaSize = allocate.getInt();
        
        allocate.clear();
    }

}
