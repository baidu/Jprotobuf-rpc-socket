package com.baidu.jprotobuf.pbrpc.transport;

import java.nio.ByteOrder;

import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;

public class RpcServerOptions {
    
    public RpcServerOptions() {
        
        tcpNoDelay = true;
        this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        keepAlive = true;
        keepAliveTime = 60;
        
    }
    
    private boolean keepAlive;
    
    /** 字节顺序 **/
    private ByteOrder byteOrder;
    
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
    
}
