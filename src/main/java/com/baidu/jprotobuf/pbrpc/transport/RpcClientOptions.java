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

/**
 * RPC client properties.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientOptions {
    
    private int connectTimeout;  // connection time out in milliseconds
    private int sendBufferSize;
    private int receiveBufferSize;
    private boolean tcpNoDelay;
    private boolean keepAlive;
    private boolean reuseAddress;
    private int idleTimeout;
    
    // connection pool settings
    private boolean shortConnection = false;
    private int threadPoolSize = 20;
    private int maxIdleSize = 8;
    private int minIdleSize = 2;
    private long minEvictableIdleTime = 1000L * 60L * 2;
    private long maxWait = 2000L; // max wait time in milliseconds for pool available
    
    // in MILLISECONDS unit
    private int onceTalkTimeout = 1000;
    
    // if use chunkSize will split chunkSize 
    private long chunkSize = -1;
    
    /**
     * time out set for chunk package wait in ms.
     */
    private int chunkPackageTimeout = 300 * 1000;

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
     * get the onceTalkTimeout
     * @return the onceTalkTimeout
     */
    public int getOnceTalkTimeout() {
        return onceTalkTimeout;
    }

    /**
     * set onceTalkTimeout value to onceTalkTimeout
     * @param onceTalkTimeout the onceTalkTimeout to set
     */
    public void setOnceTalkTimeout(int onceTalkTimeout) {
        this.onceTalkTimeout = onceTalkTimeout;
    }

    public RpcClientOptions() {
        this.connectTimeout = 1000;
        this.sendBufferSize = 1048576;
        this.receiveBufferSize = 1048576;
        this.keepAlive = true;
        this.tcpNoDelay = true;
        this.idleTimeout = 0;
        this.reuseAddress = true;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public boolean getTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    /**
     * get the shortConnection
     * @return the shortConnection
     */
    public boolean isShortConnection() {
        return shortConnection;
    }

    /**
     * set shortConnection value to shortConnection
     * @param shortConnection the shortConnection to set
     */
    public void setShortConnection(boolean shortConnection) {
        this.shortConnection = shortConnection;
    }

    /**
     * get the threadPoolSize
     * @return the threadPoolSize
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * set threadPoolSize value to threadPoolSize
     * @param threadPoolSize the threadPoolSize to set
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * get the minEvictableIdleTime
     * @return the minEvictableIdleTime
     */
    public long getMinEvictableIdleTime() {
        return minEvictableIdleTime;
    }

    /**
     * set minEvictableIdleTime value to minEvictableIdleTime
     * @param minEvictableIdleTime the minEvictableIdleTime to set
     */
    public void setMinEvictableIdleTime(long minEvictableIdleTime) {
        this.minEvictableIdleTime = minEvictableIdleTime;
    }

    /**
     * get the maxWait
     * @return the maxWait
     */
    public long getMaxWait() {
        return maxWait;
    }

    /**
     * set maxWait value to maxWait
     * @param maxWait the maxWait to set
     */
    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * get the maxIdleSize
     * @return the maxIdleSize
     */
    public int getMaxIdleSize() {
        return maxIdleSize;
    }

    /**
     * set maxIdleSize value to maxIdleSize
     * @param maxIdleSize the maxIdleSize to set
     */
    public void setMaxIdleSize(int maxIdleSize) {
        this.maxIdleSize = maxIdleSize;
    }

    /**
     * get the minIdleSize
     * @return the minIdleSize
     */
    public int getMinIdleSize() {
        return minIdleSize;
    }

    /**
     * set minIdleSize value to minIdleSize
     * @param minIdleSize the minIdleSize to set
     */
    public void setMinIdleSize(int minIdleSize) {
        this.minIdleSize = minIdleSize;
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
    
    
}
