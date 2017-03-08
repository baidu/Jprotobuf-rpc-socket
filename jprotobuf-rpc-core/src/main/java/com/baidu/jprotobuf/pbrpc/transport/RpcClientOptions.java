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

/**
 * RPC client properties.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcClientOptions {

    /** The connect timeout. */
    private int connectTimeout; // connection time out in milliseconds
    
    /** The send buffer size. */
    private int sendBufferSize;
    
    /** The receive buffer size. */
    private int receiveBufferSize;
    
    /** The tcp no delay. */
    private boolean tcpNoDelay;
    
    /** The keep alive. */
    private boolean keepAlive;
    
    /** The reuse address. */
    private boolean reuseAddress;
    
    /** The idle timeout. */
    private int idleTimeout;

    /** The short connection. */
    // connection pool settings
    private boolean shortConnection = false;
    
    /** The thread pool size. */
    private int threadPoolSize = 20;
    
    /** The max idle size. */
    private int maxIdleSize = 20;
    
    /** The min idle size. */
    private int minIdleSize = 2;
    
    /** The min evictable idle time. */
    private long minEvictableIdleTime = 1000L * 60L * 2;
    
    /** The max wait. */
    private long maxWait = 2000L; // max wait time in milliseconds for pool available
    
    /** The lifo. */
    private boolean lifo = false;
    
    /** The share thread pool under each proxy. */
    private boolean shareThreadPoolUnderEachProxy = false; // share a thread pool under each rpc proxy

    /** The test on borrow. */
    private boolean testOnBorrow = true;
    
    /** The test on return. */
    private boolean testOnReturn = false;

    /** The once talk timeout. */
    // in MILLISECONDS unit
    private int onceTalkTimeout = 1000;
    
    /** The max byte size to send and receive from buffer. */
    private int maxSize = Integer.MAX_VALUE;
    
    /** The Constant POLL_EVENT_GROUP. */
    public static final int POLL_EVENT_GROUP = 0;
    
    /** The Constant EPOLL_EVENT_GROUP. */
    public static final int EPOLL_EVENT_GROUP = 1;
    
    /** The io event group type. */
    private int ioEventGroupType = POLL_EVENT_GROUP; // 0=poll, 1=epoll

    /** The chunk size. */
    // if use chunkSize will split chunkSize
    private long chunkSize = -1;
    
    /** The jmx enabled. */
    private boolean jmxEnabled = false;

    /**
     * Copy from.
     *
     * @param options the options
     */
    public void copyFrom(RpcClientOptions options) {
        if (options == null) {
            return;
        }
        this.connectTimeout = options.connectTimeout;
        this.sendBufferSize = options.sendBufferSize;
        this.receiveBufferSize = options.receiveBufferSize;
        this.tcpNoDelay = options.tcpNoDelay;
        this.keepAlive = options.keepAlive;
        this.reuseAddress = options.reuseAddress;
        this.idleTimeout = options.idleTimeout;
        this.shortConnection = options.shortConnection;
        this.threadPoolSize = options.threadPoolSize;
        this.maxIdleSize = options.maxIdleSize;
        this.minIdleSize = options.minIdleSize;
        this.minEvictableIdleTime = options.minEvictableIdleTime;
        this.maxWait = options.maxWait;
        this.onceTalkTimeout = options.onceTalkTimeout;
        this.chunkSize = options.chunkSize;
        this.testOnBorrow = options.testOnBorrow;
        this.testOnReturn = options.testOnReturn;
        this.shareThreadPoolUnderEachProxy = options.shareThreadPoolUnderEachProxy;
        this.jmxEnabled = options.jmxEnabled;
        this.lifo = options.lifo;
        this.maxSize = options.maxSize;
        this.ioEventGroupType = options.ioEventGroupType;
    }

    /**
     * time out set for chunk package wait in ms.
     */
    private int chunkPackageTimeout = 300 * 1000;

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
     * Gets the once talk timeout.
     *
     * @return the once talk timeout
     */
    public int getOnceTalkTimeout() {
        return onceTalkTimeout;
    }

    /**
     * Sets the once talk timeout.
     *
     * @param onceTalkTimeout the new once talk timeout
     */
    public void setOnceTalkTimeout(int onceTalkTimeout) {
        this.onceTalkTimeout = onceTalkTimeout;
    }

    /**
     * Instantiates a new rpc client options.
     */
    public RpcClientOptions() {
        this.connectTimeout = 1000;
        this.sendBufferSize = 1048576;
        this.receiveBufferSize = 1048576;
        this.keepAlive = true;
        this.tcpNoDelay = true;
        this.idleTimeout = 0;
        this.reuseAddress = true;
    }

    /**
     * Gets the connect timeout.
     *
     * @return the connect timeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout.
     *
     * @param connectTimeout the new connect timeout
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

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
     * Gets the tcp no delay.
     *
     * @return the tcp no delay
     */
    public boolean getTcpNoDelay() {
        return tcpNoDelay;
    }

    /**
     * Sets the tcp no delay.
     *
     * @param tcpNoDelay the new tcp no delay
     */
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
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
     * Gets the idle timeout.
     *
     * @return the idle timeout
     */
    public int getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * Sets the idle timeout.
     *
     * @param idleTimeout the new idle timeout
     */
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * Checks if is reuse address.
     *
     * @return true, if is reuse address
     */
    public boolean isReuseAddress() {
        return reuseAddress;
    }

    /**
     * Sets the reuse address.
     *
     * @param reuseAddress the new reuse address
     */
    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    /**
     * Checks if is short connection.
     *
     * @return true, if is short connection
     */
    public boolean isShortConnection() {
        return shortConnection;
    }

    /**
     * Sets the short connection.
     *
     * @param shortConnection the new short connection
     */
    public void setShortConnection(boolean shortConnection) {
        this.shortConnection = shortConnection;
    }

    /**
     * Gets the thread pool size.
     *
     * @return the thread pool size
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * Sets the thread pool size.
     *
     * @param threadPoolSize the new thread pool size
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * Gets the min evictable idle time.
     *
     * @return the min evictable idle time
     */
    public long getMinEvictableIdleTime() {
        return minEvictableIdleTime;
    }

    /**
     * Sets the min evictable idle time.
     *
     * @param minEvictableIdleTime the new min evictable idle time
     */
    public void setMinEvictableIdleTime(long minEvictableIdleTime) {
        this.minEvictableIdleTime = minEvictableIdleTime;
    }

    /**
     * Gets the max wait.
     *
     * @return the max wait
     */
    public long getMaxWait() {
        return maxWait;
    }

    /**
     * Sets the max wait.
     *
     * @param maxWait the new max wait
     */
    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * Gets the max idle size.
     *
     * @return the max idle size
     */
    public int getMaxIdleSize() {
        return maxIdleSize;
    }

    /**
     * Sets the max idle size.
     *
     * @param maxIdleSize the new max idle size
     */
    public void setMaxIdleSize(int maxIdleSize) {
        this.maxIdleSize = maxIdleSize;
    }

    /**
     * Gets the min idle size.
     *
     * @return the min idle size
     */
    public int getMinIdleSize() {
        return minIdleSize;
    }

    /**
     * Sets the min idle size.
     *
     * @param minIdleSize the new min idle size
     */
    public void setMinIdleSize(int minIdleSize) {
        this.minIdleSize = minIdleSize;
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
     * Checks if is test on borrow.
     *
     * @return true, if is test on borrow
     */
    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    /**
     * Sets the test on borrow.
     *
     * @param testOnBorrow the new test on borrow
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    /**
     * Checks if is test on return.
     *
     * @return true, if is test on return
     */
    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    /**
     * Sets the test on return.
     *
     * @param testOnReturn the new test on return
     */
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    /**
     * Checks if is share thread pool under each proxy.
     *
     * @return true, if is share thread pool under each proxy
     */
    public boolean isShareThreadPoolUnderEachProxy() {
        return shareThreadPoolUnderEachProxy;
    }

    /**
     * Sets the share thread pool under each proxy.
     *
     * @param shareThreadPoolUnderEachProxy the new share thread pool under each proxy
     */
    public void setShareThreadPoolUnderEachProxy(boolean shareThreadPoolUnderEachProxy) {
        this.shareThreadPoolUnderEachProxy = shareThreadPoolUnderEachProxy;
    }

    /**
     * Checks if is jmx enabled.
     *
     * @return true, if is jmx enabled
     */
    public boolean isJmxEnabled() {
        return jmxEnabled;
    }
    
    /**
     * Sets the jmx enabled.
     *
     * @param jmxEnabled the new jmx enabled
     */
    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }
    
    /**
     * Sets the lifo.
     *
     * @param lifo the new lifo
     */
    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }
    
    /**
     * Checks if is lifo.
     *
     * @return true, if is lifo
     */
    public boolean isLifo() {
        return lifo;
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
	 * Sets the max buffer size receive.
	 *
	 * @param maxSize the new max size
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
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
