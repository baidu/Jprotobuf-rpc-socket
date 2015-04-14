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
package com.baidu.jprotobuf.pbrpc.transport;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.ByteOrder;

public class RpcServerOptions {
    
    public RpcServerOptions() {
        
        tcpNoDelay = true;
        this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        keepAlive = true;
        keepAliveTime = 60;
        
    }
    
    private boolean keepAlive;
    
    /** 字节顺序 **/
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    
    /**
     * so linger
     */
    private int soLinger = 5;
    
    /**
     * backlog
     */
    private int backlog = 100;
    
    /**
     * receive buffer size
     */
    private int receiveBufferSize = 1024 * 64;
    
    /**
     * send buffer size
     */
    private int sendBufferSize = 1024 * 64;
    
    /**
     * an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE}
     * will be triggered when no read was performed for the specified period of
     * time. Specify {@code 0} to disable.
     */
    private int readerIdleTime = 60 * 30;
    
    /**
     * an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE}
     * will be triggered when no write was performed for the specified period of
     * time. Specify {@code 0} to disable.
     */
    private int writerIdleTime = 60 * 30;
    
    /**
     * connect timeout, in milliseconds
     */
    private int connectTimeout;
    
    private int keepAliveTime; // keepAlive时间（second）

    /**
     * time out set for chunk package wait in ms.
     */
    private int chunkPackageTimeout = -1;

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }
    
    /**
     * use TcpNoDelay or not
     */
    public boolean tcpNoDelay = true;
    
    public int getSendBufferSize() {
        return sendBufferSize;
    }
    
    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }
    
    public int getSoLinger() {
        return soLinger;
    }
    
    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }
    
    public int getBacklog() {
        return backlog;
    }
    
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }
    
    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }
    
    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }
    
    public int getReaderIdleTime() {
        return readerIdleTime;
    }
    
    public void setReaderIdleTime(int readerIdleTime) {
        this.readerIdleTime = readerIdleTime;
    }
    
    public int getWriterIdleTime() {
        return writerIdleTime;
    }
    
    public void setWriterIdleTime(int writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

    /**
     * get the tcpNoDelay
     * @return the tcpNoDelay
     */
    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    /**
     * set tcpNoDelay value to tcpNoDelay
     * @param tcpNoDelay the tcpNoDelay to set
     */
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    /**
     * get the byteOrder
     * @return the byteOrder
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * set byteOrder value to byteOrder
     * @param byteOrder the byteOrder to set
     */
    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    /**
     * get the connectTimeout
     * @return the connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * set connectTimeout value to connectTimeout
     * @param connectTimeout the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * get the keepAlive
     * @return the keepAlive
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * set keepAlive value to keepAlive
     * @param keepAlive the keepAlive to set
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    /**
     * get the chunkPackageTimeout
     * @return the chunkPackageTimeout
     */
    public int getChunkPackageTimeout() {
        return chunkPackageTimeout;
    }

    /**
     * set chunkPackageTimeout value to chunkPackageTimeout
     * @param chunkPackageTimeout the chunkPackageTimeout to set
     */
    public void setChunkPackageTimeout(int chunkPackageTimeout) {
        this.chunkPackageTimeout = chunkPackageTimeout;
    }
    
}
