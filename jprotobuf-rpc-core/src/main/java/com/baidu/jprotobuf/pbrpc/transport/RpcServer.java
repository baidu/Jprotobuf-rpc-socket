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

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.management.HttpServer;
import com.baidu.jprotobuf.pbrpc.server.IDLServiceExporter;
import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * RPC server provider by Netty server.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServer extends ServerBootstrap {

    /** The Constant DEFAULT_WAIT_STOP_INTERVAL. */
    private static final int DEFAULT_WAIT_STOP_INTERVAL = 200;

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(RpcServer.class.getName());

    /** The stop. */
    private AtomicBoolean stop = new AtomicBoolean(false);

    /** The rpc server options. */
    private RpcServerOptions rpcServerOptions;

    /** The rpc server pipeline initializer. */
    private RpcServerPipelineInitializer rpcServerPipelineInitializer;

    /** The boss group. */
    private EventLoopGroup bossGroup;
    
    /** The worker group. */
    private EventLoopGroup workerGroup;
    
    /** The channel. */
    private Channel channel;

    /** The inet socket address. */
    private InetSocketAddress inetSocketAddress;

    /** The start time. */
    private long startTime;

    /** The http server. */
    private HttpServer httpServer;

    /** The blockingqueue. */
    private BlockingQueue<Runnable> blockingqueue = new LinkedBlockingQueue<Runnable>();
    
    /** The es. */
    private ThreadPoolExecutor es;

    /**
     * Sets the interceptor.
     *
     * @param interceptor the new interceptor
     */
    public void setInterceptor(InvokerInterceptor interceptor) {
        if (rpcServiceRegistry != null) {
            rpcServiceRegistry.setInterceptor(interceptor);
        }
    }

    /**
     * Gets the inet socket address.
     *
     * @return the inet socket address
     */
    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    /**
     * Gets the es.
     *
     * @return the es
     */
    public ThreadPoolExecutor getEs() {
        return es;
    }

    /** rpcServiceRegistry. */
    private RpcServiceRegistry rpcServiceRegistry;

    /**
     * Instantiates a new rpc server.
     *
     * @param serverChannelClass the server channel class
     * @param serverOptions the server options
     * @param rpcServiceRegistry the rpc service registry
     */
    public RpcServer(Class<? extends ServerChannel> serverChannelClass, RpcServerOptions serverOptions,
            RpcServiceRegistry rpcServiceRegistry) {
        if (rpcServiceRegistry == null) {
            throw new RuntimeException("protperty 'rpcServiceRegistry ' is null.");
        }

        if (serverOptions == null) {
            serverOptions = new RpcServerOptions();
        }

        if (serverOptions.getIoEventGroupType() == RpcServerOptions.POLL_EVENT_GROUP) {
            this.bossGroup = new NioEventLoopGroup(serverOptions.getAcceptorThreads());
            this.workerGroup = new NioEventLoopGroup(serverOptions.getWorkThreads());
        } else {
            this.bossGroup = new EpollEventLoopGroup(serverOptions.getAcceptorThreads());
            this.workerGroup = new EpollEventLoopGroup(serverOptions.getWorkThreads());
        }

        if (serverOptions.getTaskTheads() > 0) {
            es = new ThreadPoolExecutor(serverOptions.getTaskTheads(), serverOptions.getTaskTheads(), 60L,
                    TimeUnit.SECONDS, blockingqueue);
        }

        this.group(this.bossGroup, this.workerGroup);
        this.channel(serverChannelClass);

        this.option(ChannelOption.SO_BACKLOG, serverOptions.getBacklog());

        this.childOption(ChannelOption.SO_KEEPALIVE, serverOptions.isKeepAlive());
        this.childOption(ChannelOption.SO_REUSEADDR, true);
        this.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.childOption(ChannelOption.TCP_NODELAY, serverOptions.isTcpNoDelay());
        this.childOption(ChannelOption.SO_LINGER, serverOptions.getSoLinger());
        this.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverOptions.getConnectTimeout());
        this.childOption(ChannelOption.SO_RCVBUF, serverOptions.getReceiveBufferSize());
        this.childOption(ChannelOption.SO_SNDBUF, serverOptions.getSendBufferSize());

        this.rpcServiceRegistry = rpcServiceRegistry;
        // do register meta service
        rpcServiceRegistry.doRegisterMetaService();
        this.rpcServerOptions = serverOptions;
        this.rpcServerPipelineInitializer = new RpcServerPipelineInitializer(rpcServiceRegistry, rpcServerOptions, es);
        this.childHandler(rpcServerPipelineInitializer);
    }

    /**
     * Instantiates a new rpc server.
     *
     * @param serverOptions the server options
     */
    public RpcServer(RpcServerOptions serverOptions) {
        this(NioServerSocketChannel.class, serverOptions, new RpcServiceRegistry());
    }

    /**
     * Instantiates a new rpc server.
     *
     * @param serverOptions the server options
     * @param rpcServiceRegistry the rpc service registry
     */
    public RpcServer(RpcServerOptions serverOptions, RpcServiceRegistry rpcServiceRegistry) {
        this(NioServerSocketChannel.class, serverOptions, rpcServiceRegistry);
    }

    /**
     * Instantiates a new rpc server.
     */
    public RpcServer() {
        this(new RpcServerOptions());
    }

    /**
     * Instantiates a new rpc server.
     *
     * @param serverChannelClass the server channel class
     */
    public RpcServer(Class<? extends ServerChannel> serverChannelClass) {
        this(serverChannelClass, new RpcServerOptions(), new RpcServiceRegistry());
    }

    /**
     * Register service.
     *
     * @param service the service
     */
    public void registerService(IDLServiceExporter service) {
        rpcServiceRegistry.registerService(service);
    }

    /**
     * Register service.
     *
     * @param target the target
     */
    public void registerService(final Object target) {
        rpcServiceRegistry.registerService(target);
    }

    /**
     * Register dynamic service.
     *
     * @param methodSignature the method signature
     * @param method the method
     * @param service the service
     * @param cls the cls
     */
    public void registerDynamicService(String methodSignature, Method method, Object service,
            Class<? extends ServerAttachmentHandler> cls) {
        rpcServiceRegistry.doDynamicRegisterService(methodSignature, method, service, cls);
    }
    
    /**
     * remove service by method signature. if method signature not exist nothing to do.
     * 
     * @param methodSignature target method signature to remove.
     */
    public void unRegisterDynamicService(String methodSignature) {
        
        rpcServiceRegistry.unRegisterDynamicService(methodSignature);
    }
    
    /**
     * Register dynamic service.
     *
     * @param methodSignature the method signature
     * @param method the method
     * @param service the service
     * @param cls the cls
     */
    public void registerDynamicService(String serviceName, String methodName, Method method, Object service,
            Class<? extends ServerAttachmentHandler> cls) {
        rpcServiceRegistry.doDynamicRegisterService(serviceName, methodName, method, service, cls);
    }

    /**
     * Start.
     *
     * @param port the port
     */
    public void start(int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        start(inetSocketAddress);
    }
    
    /**
     * Start.
     *
     * @param port the port
     */
    public void startSync(int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        startSync(inetSocketAddress);
    }

    /**
     * Start.
     *
     * @param sa the sa
     */
    public void startSync(final InetSocketAddress sa) {
        LOG.log(Level.INFO, "RPC starting at: " + sa);
        
        try {
            this.bind(sa).sync();
        } catch (Throwable e) {
            shutdown();
            throw new RuntimeException(e.getMessage(), e);
        }
        
    }
    
    public void start(final InetSocketAddress sa) {
        LOG.log(Level.INFO, "RPC starting at: " + sa);
        
        this.bind(sa).addListener(new ChannelFutureListener() {
            
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel = future.channel();
                    initAfterBindPort(sa);
                } else {
                    shutdown();
                    throw new Exception("bind port failed:" + sa.toString() + " message:" + future.toString());
                    
                }
            }
        });
    }
    
    
    

    /**
     * Inits the after bind port.
     *
     * @param sa the sa
     */
    protected void initAfterBindPort(final InetSocketAddress sa) {
        this.inetSocketAddress = sa;

        startTime = System.currentTimeMillis();

        // check if need start http server
        if (rpcServerOptions.getHttpServerPort() > 0) {
            httpServer = new HttpServer(this);
            httpServer.start(rpcServerOptions.getHttpServerPort());
        }
    }

    /**
     * Wait for stop.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void waitForStop() throws InterruptedException {
        while (!stop.get()) {
            Thread.sleep(DEFAULT_WAIT_STOP_INTERVAL);
        }
        shutdown();
    }

    /**
     * Stop.
     */
    public void stop() {
        stop.set(true);
    }

    /**
     * Gets the stop.
     *
     * @return the stop
     */
    public AtomicBoolean getStop() {
        return stop;
    }

    /**
     * Checks if is stop.
     *
     * @return true, if is stop
     */
    public boolean isStop() {
        return stop.get();
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        stop();
        if (channel != null && channel.isOpen()) {
            channel.close();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        if (es != null) {
            es.shutdown();
        }

        if (httpServer != null) {
            httpServer.shutdownNow();
        }

    }

    /**
     * Sets the stop.
     *
     * @param stop the new stop
     */
    public void setStop(AtomicBoolean stop) {
        this.stop = stop;
    }

    /**
     * Gets the rpc server options.
     *
     * @return the rpc server options
     */
    public RpcServerOptions getRpcServerOptions() {
        return rpcServerOptions;
    }

    /**
     * Sets the rpc server options.
     *
     * @param rpcServerOptions the new rpc server options
     */
    public void setRpcServerOptions(RpcServerOptions rpcServerOptions) {
        this.rpcServerOptions = rpcServerOptions;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }
}
