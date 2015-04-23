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

package com.baidu.jprotobuf.pbrpc.transport.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.ErrorDataException;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.baidu.jprotobuf.pbrpc.data.RpcMeta;
import com.baidu.jprotobuf.pbrpc.server.RpcData;
import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;

/**
 * RPC service handler on request data arrived.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RpcServiceHandler extends SimpleChannelInboundHandler<RpcDataPackage> {

    /**
     * log this class
     */
    private static final Logger LOG = Logger.getLogger(RpcServiceHandler.class.getName());

    /**
     * {@link RpcServiceRegistry}
     */
    private final RpcServiceRegistry rpcServiceRegistry;

    /**
     * @param rpcServiceRegistry
     */
    public RpcServiceHandler(RpcServiceRegistry rpcServiceRegistry) {
        this.rpcServiceRegistry = rpcServiceRegistry;
    }
    
    @Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcDataPackage dataPackage)
			throws Exception {
    	 try {
             RpcMeta rpcMeta = dataPackage.getRpcMeta();
             String serviceName = rpcMeta.getRequest().getSerivceName();
             String methodName = rpcMeta.getRequest().getMethodName();

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

                 RpcData response = handler.doHandle(request);
                 dataPackage.data(response.getData());
                 dataPackage.attachment(response.getAttachment());
                 dataPackage.authenticationData(response.getAuthenticationData());

                 dataPackage.errorCode(ErrorCodes.ST_SUCCESS);
                 dataPackage.errorText(null);
             }

             // We do not need to write a ChannelBuffer here.
             // We know the encoder inserted at TelnetPipelineFactory will do the
             // conversion.
             ctx.writeAndFlush(dataPackage);
         } catch (InvocationTargetException t) {
             Throwable error = t.getTargetException();
             ErrorDataException exception = new ErrorDataException(error.getMessage(), error);
             exception.setErrorCode(ErrorCodes.ST_ERROR);
             exception.setRpcDataPackage(dataPackage);
             throw exception;
         } catch (Exception t) {
             ErrorDataException exception = new ErrorDataException(t.getMessage(), t);
             exception.setErrorCode(ErrorCodes.ST_ERROR);
             exception.setRpcDataPackage(dataPackage);
             throw exception;
         }
	}

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
            data = data.magicCode(ProtocolConstant.MAGIC_CODE).getErrorResponseRpcDataPackage(
                    ErrorCodes.ST_ERROR, cause.getCause().getMessage());
        }
        ctx.fireChannelRead(data);
    }

	

}
