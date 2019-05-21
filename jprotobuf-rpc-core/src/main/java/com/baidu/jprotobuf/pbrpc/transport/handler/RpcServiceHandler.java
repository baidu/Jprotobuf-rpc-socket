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

package com.baidu.jprotobuf.pbrpc.transport.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ErrorDataException;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcMeta;
import com.baidu.jprotobuf.pbrpc.server.RpcData;
import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;
import com.baidu.jprotobuf.pbrpc.transport.ExceptionCatcher;
import com.baidu.jprotobuf.pbrpc.transport.RpcErrorMessage;
import com.baidu.jprotobuf.pbrpc.utils.LogIdThreadLocalHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * RPC service handler on request data arrived.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServiceHandler extends SimpleChannelInboundHandler<RpcDataPackage> {

    /** log this class. */
    private static final Logger LOG = Logger.getLogger(RpcServiceHandler.class.getName());

    /** The es. */
    private ExecutorService es;

    /**
     * Sets the es.
     *
     * @param es the new es
     */
    public void setEs(ExecutorService es) {
        this.es = es;
    }

    /** {@link RpcServiceRegistry}. */
    private final RpcServiceRegistry rpcServiceRegistry;

    /** The exception catcher. */
    private ExceptionCatcher exceptionCatcher;
    
    /**
     * Instantiates a new rpc service handler.
     *
     * @param rpcServiceRegistry the rpc service registry
     * @param exceptionCatcher the exception catcher
     */
    public RpcServiceHandler(RpcServiceRegistry rpcServiceRegistry, ExceptionCatcher exceptionCatcher) {
        this.rpcServiceRegistry = rpcServiceRegistry;
        this.exceptionCatcher = exceptionCatcher;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
     * java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDataPackage dataPackage) throws Exception {
        BackgroundTask task = new BackgroundTask(ctx, dataPackage, rpcServiceRegistry, exceptionCatcher);

        if (es != null) {
            // run by async way
            es.submit(task);
        } else {
            task.run(); // run at current thread
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext,
     * java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.log(Level.SEVERE, cause.getCause().getMessage(), cause.getCause());

        RpcDataPackage data = null;

        if (cause instanceof ErrorDataException) {
            ErrorDataException error = (ErrorDataException) cause;
            RpcDataPackage dataPackage = error.getRpcDataPackage();
            if (dataPackage != null) {
                int errorCode = ErrorCodes.ST_ERROR;
                if (error.getErrorCode() > 0) {
                    errorCode = error.getErrorCode();
                }
                data = dataPackage.getErrorResponseRpcDataPackage(errorCode, cause.getCause().getMessage());

                handleException(data, exceptionCatcher, (ErrorDataException) cause);
            }
        }

        if (data == null) {
            data = new RpcDataPackage();
            data = data.magicCode(ProtocolConstant.MAGIC_CODE).getErrorResponseRpcDataPackage(ErrorCodes.ST_ERROR,
                    cause.getCause().getMessage());
        }

        ctx.fireChannelRead(data);
    }

    /**
     * The Class BackgroundTask.
     */
    private static class BackgroundTask implements Runnable {

        /** The ctx. */
        private ChannelHandlerContext ctx;

        /** The data package. */
        private RpcDataPackage dataPackage;

        /** The rpc service registry. */
        private RpcServiceRegistry rpcServiceRegistry;

        private ExceptionCatcher exceptionCatcher;

        /**
         * Instantiates a new background task.
         *
         * @param ctx the ctx
         * @param dataPackage the data package
         * @param rpcServiceRegistry the rpc service registry
         */
        public BackgroundTask(ChannelHandlerContext ctx, RpcDataPackage dataPackage,
                RpcServiceRegistry rpcServiceRegistry, ExceptionCatcher exceptionCatcher) {
            super();
            this.ctx = ctx;
            this.dataPackage = dataPackage;
            this.rpcServiceRegistry = rpcServiceRegistry;
            this.exceptionCatcher = exceptionCatcher;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            long time = System.currentTimeMillis();

            if (dataPackage.getRpcMeta().getResponse() != null) {
                Integer errorCode = dataPackage.getRpcMeta().getResponse().getErrorCode();
                if (errorCode != null && errorCode > 0) {
                    dataPackage.data(null);
                    dataPackage.attachment(null);
                    ctx.writeAndFlush(dataPackage);
                    return;
                }
            }
            

            RpcMeta rpcMeta = dataPackage.getRpcMeta();
            String serviceName = rpcMeta.getRequest().getSerivceName();
            String methodName = rpcMeta.getRequest().getMethodName();
            
            // check if async mode
            boolean asyncMode = rpcServiceRegistry.isAsyncMode(serviceName, methodName);
            if (asyncMode) {
                
                RpcDataPackage copy = dataPackage.copy();
                
                copy.errorCode(ErrorCodes.ST_SUCCESS);
                copy.data(null);
                copy.attachment(null);
                ctx.writeAndFlush(copy);
            }

            Long logId = rpcMeta.getRequest().getLogId();
            // set log id to holder
            LogIdThreadLocalHolder.setLogId(logId);
            try {
                RpcHandler handler = rpcServiceRegistry.lookupService(serviceName, methodName);
                if (handler == null) {
                    String message = "service name '" + serviceName + "' and methodName '" + methodName + "' not found";
                    LOG.log(Level.WARNING, message);
                    dataPackage.errorCode(ErrorCodes.ST_SERVICE_NOTFOUND);
                    dataPackage.errorText(message);
                } else {

                    byte[] data = dataPackage.getData();
                    RpcData request = new RpcData();
                    request.setLogId(dataPackage.getRpcMeta().getRequest().getLogId());
                    request.setData(data);
                    request.setAttachment(dataPackage.getAttachment());
                    if (dataPackage.getRpcMeta() != null) {
                        request.setAuthenticationData(dataPackage.getRpcMeta().getAuthenticationData());
                    }
                    request.setExtraParams(dataPackage.getRpcMeta().getRequest().getExtraParam());
                    try {
                        RpcData response = handler.doHandle(request);
                        dataPackage.data(response.getData());
                        dataPackage.attachment(response.getAttachment());
                        dataPackage.authenticationData(response.getAuthenticationData());

                        dataPackage.errorCode(ErrorCodes.ST_SUCCESS);
                        dataPackage.errorText(null);

                    } catch (InvocationTargetException e) {
                        Throwable targetException = e.getTargetException();
                        if (targetException == null) {
                            targetException = e;
                        }

                        LOG.log(Level.SEVERE, targetException.getMessage(), targetException);
                        // catch business exception
                        dataPackage.errorCode(ErrorCodes.ST_ERROR);
                        dataPackage.errorText(targetException.getMessage());
                        handleException(dataPackage, exceptionCatcher, e);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e.getCause());
                        // catch business exception
                        dataPackage.errorCode(ErrorCodes.ST_ERROR);
                        dataPackage.errorText(e.getMessage());
                        handleException(dataPackage, exceptionCatcher, e);
                    }
                }

                // We do not need to write a ChannelBuffer here.
                // We know the encoder inserted at TelnetPipelineFactory will do
                // the
                // conversion.
                if (!asyncMode) {
                    ctx.writeAndFlush(dataPackage);
                }
            } catch (Exception t) {
                ErrorDataException exception = new ErrorDataException(t.getMessage(), t);
                exception.setErrorCode(ErrorCodes.ST_ERROR);
                exception.setRpcDataPackage(dataPackage);
                throw new RuntimeException(exception.getMessage(), exception);
            } finally {
                LOG.fine("RPC server invoke method '" + methodName + "' time took:"
                        + (System.currentTimeMillis() - time) + " ms");

                LogIdThreadLocalHolder.clearLogId();
            }
        }

    }

    /**
     * Handle exception.
     *
     * @param rpcDataPackage the rpc data package
     * @param exceptionCatcher the exception catcher
     * @param e the e
     */
    private static void handleException(RpcDataPackage rpcDataPackage, ExceptionCatcher exceptionCatcher, Exception e) {
        if (exceptionCatcher == null) {
            return;
        }

        RpcErrorMessage rpcErrorMessage = exceptionCatcher.onException(e);
        if (rpcErrorMessage != null) {
            rpcDataPackage.errorCode(rpcErrorMessage.getErrorCode());
            rpcDataPackage.errorText(rpcErrorMessage.getErrorMessage());
        }

    }

}
