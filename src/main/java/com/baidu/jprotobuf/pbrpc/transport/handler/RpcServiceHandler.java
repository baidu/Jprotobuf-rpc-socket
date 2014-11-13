/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.baidu.jprotobuf.pbrpc.RpcHandler;
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
public class RpcServiceHandler extends SimpleChannelUpstreamHandler {

    /**
     * 
     */
    private final RpcServiceRegistry rpcServiceRegistry;

    /**
     * @param rpcServiceRegistry
     */
    public RpcServiceHandler(RpcServiceRegistry rpcServiceRegistry) {
        super();
        this.rpcServiceRegistry = rpcServiceRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(
     * org.jboss.netty.channel.ChannelHandlerContext,
     * org.jboss.netty.channel.MessageEvent)
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        if (!(e.getMessage() instanceof RpcDataPackage)) {
            return;
        }
        RpcDataPackage dataPackage = (RpcDataPackage) e.getMessage();
        
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
            request.setData(data);
            request.setAttachment(dataPackage.getAttachment());
            if (dataPackage.getRpcMeta() != null) {
                request.setAuthenticationData(dataPackage.getRpcMeta().getAuthenticationData());
            }
            
            RpcData response = handler.doHandle(request);
            dataPackage.data(response.getData());
            dataPackage.attachment(response.getAttachment());
            dataPackage.authenticationData(response.getAuthenticationData());
            
            dataPackage.errorCode(ErrorCodes.ST_SUCCESS);
            dataPackage.errorText(null);
        }
        
        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
        e.getChannel().write(dataPackage);
        
    }

}
