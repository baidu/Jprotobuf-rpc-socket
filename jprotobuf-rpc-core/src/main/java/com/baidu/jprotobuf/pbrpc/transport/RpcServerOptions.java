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
package com.baidu.jprotobuf.pbrpc.transport;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.ByteOrder;

/**
 * The Class RpcServerOptions.
 */
public class RpcServerOptions {

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RpcServerOptions [keepAlive=");
        builder.append(keepAlive);
        builder.append(", byteOrder=");
        builder.append(byteOrder);
        builder.append(", soLinger=");
        builder.append(soLinger);
        builder.append(", backlog=");
        builder.append(backlog);
        builder.append(", receiveBufferSize=");
        builder.append(receiveBufferSize);
        builder.append(", sendBufferSize=");
        builder.append(sendBufferSize);
        builder.append(", readerIdleTime=");
        builder.append(readerIdleTime);
        builder.append(", writerIdleTime=");
        builder.append(writerIdleTime);
        builder.append(", connectTimeout=");
        builder.append(connectTimeout);
        builder.append(", keepAliveTime=");
        builder.append(keepAliveTime);
        builder.append(", chunkPackageTimeout=");
        builder.append(chunkPackageTimeout);
        builder.append(", acceptorThreads=");
        builder.append(acceptorThreads);
        builder.append(", workThreads=");
        builder.append(workThreads);
        builder.append(", taskTheads=");
        builder.append(taskTheads);
        builder.append(", chunkSize=");
        builder.append(chunkSize);
        builder.append(", httpServerPort=");
        builder.append(httpServerPort);
        builder.append(", tcpNoDelay=");
        builder.append(tcpNoDelay);
        builder.append(", ioEventGroupType=");
        builder.append(ioEventGroupType);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Instantiates a new rpc server options.
     */
    public RpcServerOptions() {

        tcpNoDelay = true;
        this.byteOrder = ByteOrder.BIG_ENDIAN;
        keepAlive = true;
        keepAliveTime = 60;

    }

    /** The keep alive. */
    private boolean keepAlive;

    /** 字节顺序 *. */
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    /** so linger. */
    private int soLinger = 5;

    /** backlog. */
    private int backlog = 100;

    /** receive buffer size. */
    private int receiveBufferSize = 1024 * 64;

    /** send buffer size. */
    private int sendBufferSize = 1024 * 64;

    /**
     * an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be triggered when no read was
     * performed for the specified period of time. Specify {@code 0} to disable.
     */
    private int readerIdleTime = 60;

    /**
     * an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be triggered when no write was
     * performed for the specified period of time. Specify {@code 0} to disable.
     */
    private int writerIdleTime = 60;

    /** The keep alive time.  all idle time in seconds */
    private int keepAliveTime; // keepAlive时间（second）
    
    /** connect timeout, in milliseconds. */
    private int connectTimeout;

    /**
     * time out set for chunk package wait in ms.
     */
    private int chunkPackageTimeout = 300 * 1000;

    /** The acceptor threads. */
    private int acceptorThreads = 0; // acceptor threads. default use Netty default value

    /** The work threads. */
    private int workThreads = 0; // work threads. default use Netty default value

    /** The task theads. */
    private int taskTheads = 0; // real execute task threads

    /** The chunk size. */
    // if use chunkSize will split chunkSize
    private long chunkSize = -1;

    /** The max size. */
    private int maxSize = Integer.MAX_VALUE;

    /** The Constant POLL_EVENT_GROUP. */
    public static final int POLL_EVENT_GROUP = 0;

    /** The Constant EPOLL_EVENT_GROUP. */
    public static final int EPOLL_EVENT_GROUP = 1;

    /** The io event group type. */
    private int ioEventGroupType = POLL_EVENT_GROUP; // 0=poll, 1=epoll

    /** if http server port > 0 will start http server. */
    private int httpServerPort = -1;

    /**
     * Copy from.
     *
     * @param options the options
     */
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
        this.taskTheads = options.taskTheads;
        this.httpServerPort = options.httpServerPort;
        this.maxSize = options.maxSize;
        this.ioEventGroupType = options.ioEventGroupType;
    }

    /**
     * Gets the chunk size.
     *
     * @return the chunk size
     */
    public long getChunkSize() {
        return chunkSize;
    }

    /**
     * Sets the chunk size.
     *
     * @param chunkSize the new chunk size
     */
    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * Gets the keep alive time.
     *
     * @return the keep alive time
     */
    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Sets the keep alive time.
     *
     * @param keepAliveTime the new keep alive time
     */
    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    /** use TcpNoDelay or not. */
    public boolean tcpNoDelay = true;

    /**
     * Gets the send buffer size.
     *
     * @return the send buffer size
     */
    public int getSendBufferSize() {
        return sendBufferSize;
    }

    /**
     * Sets the send buffer size.
     *
     * @param sendBufferSize the new send buffer size
     */
    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    /**
     * Gets the so linger.
     *
     * @return the so linger
     */
    public int getSoLinger() {
        return soLinger;
    }

    /**
     * Sets the so linger.
     *
     * @param soLinger the new so linger
     */
    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    /**
     * Gets the backlog.
     *
     * @return the backlog
     */
    public int getBacklog() {
        return backlog;
    }

    /**
     * Sets the backlog.
     *
     * @param backlog the new backlog
     */
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    /**
     * Gets the receive buffer size.
     *
     * @return the receive buffer size
     */
    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    /**
     * Sets the receive buffer size.
     *
     * @param receiveBufferSize the new receive buffer size
     */
    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    /**
     * Gets the an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be triggered when no read
     * was performed for the specified period of time.
     *
     * @return the an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be triggered when no read
     *         was performed for the specified period of time
     */
    public int getReaderIdleTime() {
        return readerIdleTime;
    }

    /**
     * Sets the an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be triggered when no read
     * was performed for the specified period of time.
     *
     * @param readerIdleTime the new an {@link IdleStateEvent} whose state is {@link IdleState#READER_IDLE} will be
     *            triggered when no read was performed for the specified period of time
     */
    public void setReaderIdleTime(int readerIdleTime) {
        this.readerIdleTime = readerIdleTime;
    }

    /**
     * Gets the an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be triggered when no write
     * was performed for the specified period of time.
     *
     * @return the an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be triggered when no
     *         write was performed for the specified period of time
     */
    public int getWriterIdleTime() {
        return writerIdleTime;
    }

    /**
     * Sets the an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be triggered when no write
     * was performed for the specified period of time.
     *
     * @param writerIdleTime the new an {@link IdleStateEvent} whose state is {@link IdleState#WRITER_IDLE} will be
     *            triggered when no write was performed for the specified period of time
     */
    public void setWriterIdleTime(int writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

    /**
     * Checks if is use TcpNoDelay or not.
     *
     * @return the use TcpNoDelay or not
     */
    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    /**
     * Sets the use TcpNoDelay or not.
     *
     * @param tcpNoDelay the new use TcpNoDelay or not
     */
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    /**
     * Gets the 字节顺序 *.
     *
     * @return the 字节顺序 *
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    /**
     * Sets the 字节顺序 *.
     *
     * @param byteOrder the new 字节顺序 *
     */
    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    /**
     * Gets the connect timeout, in milliseconds.
     *
     * @return the connect timeout, in milliseconds
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout, in milliseconds.
     *
     * @param connectTimeout the new connect timeout, in milliseconds
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Checks if is keep alive.
     *
     * @return true, if is keep alive
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * Sets the keep alive.
     *
     * @param keepAlive the new keep alive
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    /**
     * Gets the time out set for chunk package wait in ms.
     *
     * @return the time out set for chunk package wait in ms
     */
    public int getChunkPackageTimeout() {
        return chunkPackageTimeout;
    }

    /**
     * Sets the time out set for chunk package wait in ms.
     *
     * @param chunkPackageTimeout the new time out set for chunk package wait in ms
     */
    public void setChunkPackageTimeout(int chunkPackageTimeout) {
        this.chunkPackageTimeout = chunkPackageTimeout;
    }

    /**
     * Gets the max size.
     *
     * @return the max size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the max size.
     *
     * @param maxSize the new max size
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Gets the acceptor threads.
     *
     * @return the acceptor threads
     */
    public int getAcceptorThreads() {
        return acceptorThreads;
    }

    /**
     * Sets the acceptor threads.
     *
     * @param acceptorThreads the new acceptor threads
     */
    public void setAcceptorThreads(int acceptorThreads) {
        this.acceptorThreads = acceptorThreads;
    }

    /**
     * Gets the work threads.
     *
     * @return the work threads
     */
    public int getWorkThreads() {
        return workThreads;
    }

    /**
     * Sets the work threads.
     *
     * @param workThreads the new work threads
     */
    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    /**
     * Gets the if http server port > 0 will start http server.
     *
     * @return the if http server port > 0 will start http server
     */
    public int getHttpServerPort() {
        return httpServerPort;
    }

    /**
     * Sets the if http server port > 0 will start http server.
     *
     * @param httpServerPort the new if http server port > 0 will start http server
     */
    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    /**
     * Gets the task theads.
     *
     * @return the task theads
     */
    public int getTaskTheads() {
        return taskTheads;
    }

    /**
     * Sets the task theads.
     *
     * @param taskTheads the new task theads
     */
    public void setTaskTheads(int taskTheads) {
        this.taskTheads = taskTheads;
    }

    /**
     * Gets the io event group type.
     *
     * @return the io event group type
     */
    public int getIoEventGroupType() {
        return ioEventGroupType;
    }

    /**
     * Sets the io event group type.
     *
     * @param ioEventGroupType the new io event group type
     */
    public void setIoEventGroupType(int ioEventGroupType) {
        this.ioEventGroupType = ioEventGroupType;
    }

}
