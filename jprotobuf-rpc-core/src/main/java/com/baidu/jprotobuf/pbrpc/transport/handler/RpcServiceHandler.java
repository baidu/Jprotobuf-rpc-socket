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

	/**
	 * Instantiates a new rpc service handler.
	 *
	 * @param rpcServiceRegistry the rpc service registry
	 */
	public RpcServiceHandler(RpcServiceRegistry rpcServiceRegistry) {
		this.rpcServiceRegistry = rpcServiceRegistry;
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcDataPackage dataPackage) throws Exception {
		BackgroundTask task = new BackgroundTask(ctx, dataPackage, rpcServiceRegistry);
		
		if (es != null) {
		    // run by async way
		    es.submit(task);
		} else {
		    task.run(); // run at current thread
		}
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
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

		/**
		 * Instantiates a new background task.
		 *
		 * @param ctx the ctx
		 * @param dataPackage the data package
		 * @param rpcServiceRegistry the rpc service registry
		 */
		public BackgroundTask(ChannelHandlerContext ctx, RpcDataPackage dataPackage,
				RpcServiceRegistry rpcServiceRegistry) {
			super();
			this.ctx = ctx;
			this.dataPackage = dataPackage;
			this.rpcServiceRegistry = rpcServiceRegistry;
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
			    if (errorCode != null && errorCode > 0 ) {
			        dataPackage.data(null);
			        dataPackage.attachment(null);
			        ctx.writeAndFlush(dataPackage);
			        return;
			    }
			}
			

			RpcMeta rpcMeta = dataPackage.getRpcMeta();
			String serviceName = rpcMeta.getRequest().getSerivceName();
			String methodName = rpcMeta.getRequest().getMethodName();

			Long logId = rpcMeta.getRequest().getLogId();
			// set log id to holder
			LogIdThreadLocalHolder.setLogId(logId);
			try {
				RpcHandler handler = rpcServiceRegistry.lookupService(serviceName, methodName);
				if (handler == null) {
					dataPackage.errorCode(ErrorCodes.ST_SERVICE_NOTFOUND);
					dataPackage.errorText(ErrorCodes.MSG_SERVICE_NOTFOUND);
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
					} catch (Exception e) {
						LOG.log(Level.SEVERE, e.getMessage(), e.getCause());
						// catch business exception
						dataPackage.errorCode(ErrorCodes.ST_ERROR);
						dataPackage.errorText(e.getMessage());
					}
				}

				// We do not need to write a ChannelBuffer here.
				// We know the encoder inserted at TelnetPipelineFactory will do
				// the
				// conversion.
				ctx.writeAndFlush(dataPackage);
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

}
