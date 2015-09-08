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
    
    /**
     * 
     */
    private static final String LINE_BREAK = "\n";

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("keepAlive=");
        builder.append(keepAlive).append(LINE_BREAK);
        builder.append("byteOrder=");
        builder.append(byteOrder).append(LINE_BREAK);
        builder.append("soLinger=");
        builder.append(soLinger).append(LINE_BREAK);
        builder.append("backlog=");
        builder.append(backlog).append(LINE_BREAK);
        builder.append("receiveBufferSize=");
        builder.append(receiveBufferSize).append(LINE_BREAK);
        builder.append("sendBufferSize=");
        builder.append(sendBufferSize).append(LINE_BREAK);
        builder.append("readerIdleTime=");
        builder.append(readerIdleTime).append(LINE_BREAK);
        builder.append("writerIdleTime=");
        builder.append(writerIdleTime).append(LINE_BREAK);
        builder.append("connectTimeout=");
        builder.append(connectTimeout).append(LINE_BREAK);
        builder.append("keepAliveTime=");
        builder.append(keepAliveTime).append(LINE_BREAK);
        builder.append("chunkPackageTimeout=");
        builder.append(chunkPackageTimeout).append(LINE_BREAK);
        builder.append("acceptorThreads=");
        builder.append(acceptorThreads).append(LINE_BREAK);
        builder.append("workThreads=");
        builder.append(workThreads).append(LINE_BREAK);
        builder.append("chunkSize=");
        builder.append(chunkSize).append(LINE_BREAK);
        builder.append("httpServerPort=");
        builder.append(httpServerPort).append(LINE_BREAK);
        builder.append("tcpNoDelay=");
        builder.append(tcpNoDelay).append(LINE_BREAK);
        return builder.toString();
    }


    public RpcServerOptions() {
        
        tcpNoDelay = true;
        this.byteOrder = ByteOrder.BIG_ENDIAN;
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
    private int chunkPackageTimeout = 300 * 1000;
    
    private int acceptorThreads = 0; // acceptor threads. default use Netty default value
    private int workThreads = 0; // work threads. default use Netty default value
    
    // if use chunkSize will split chunkSize
    private long chunkSize = -1;
    
    /**
     * if http server port > 0 will start http server
     */
    private int httpServerPort = -1;
    
    public void copyFrom(RpcServerOptions options) {
        this.chunkSize = options.chunkSize;
        this.chunkPackageTimeout = options.chunkPackageTimeout;
        this.keepAliveTime = options.keepAliveTime;
        this.connectTimeout = options.connectTimeout;
        this.writerIdleTime = options.writerIdleTime;
        this.readerIdleTime = options.readerIdleTime;
        this.sendBufferSize = options.sendBufferSize;
        this.receiveBufferSize = options.receiveBufferSize;
        this.backlog = options.backlog;
        this.soLinger = options.soLinger;
        this.byteOrder = options.byteOrder;
        this.keepAlive = options.keepAlive;
        this.acceptorThreads = options.acceptorThreads;
        this.workThreads = options.workThreads;
        this.httpServerPort = options.httpServerPort;
    }
    

    /**
     * get the chunkSize
     * @return the chunkSize
     */
    public long getChunkSize() {
        return chunkSize;
    }

    /**
     * set chunkSize value to chunkSize
     * @param chunkSize the chunkSize to set
     */
    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

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


    /**
     * get the acceptorThreads
     * @return the acceptorThreads
     */
    public int getAcceptorThreads() {
        return acceptorThreads;
    }


    /**
     * set acceptorThreads value to acceptorThreads
     * @param acceptorThreads the acceptorThreads to set
     */
    public void setAcceptorThreads(int acceptorThreads) {
        this.acceptorThreads = acceptorThreads;
    }


    /**
     * get the workThreads
     * @return the workThreads
     */
    public int getWorkThreads() {
        return workThreads;
    }


    /**
     * set workThreads value to workThreads
     * @param workThreads the workThreads to set
     */
    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }
    
    /**
     * get the httpServerPort
     * @return the httpServerPort
     */
    public int getHttpServerPort() {
        return httpServerPort;
    }
    
    /**
     * set httpServerPort value to httpServerPort
     * @param httpServerPort the httpServerPort to set
     */
    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }
}
