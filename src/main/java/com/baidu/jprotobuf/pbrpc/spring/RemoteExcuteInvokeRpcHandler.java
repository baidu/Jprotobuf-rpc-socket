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
package com.baidu.jprotobuf.pbrpc.spring;

import java.util.Map;

import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.SerializationUtils;

import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware;
import com.baidu.jprotobuf.pbrpc.server.AbstractRpcHandler;
import com.baidu.jprotobuf.pbrpc.server.RpcData;


/**
 * {@link RemoteExcuteInvokeRpcHandler}
 * @author xiemalin
 * @since 2.17
 */
public class RemoteExcuteInvokeRpcHandler implements RpcHandler, RpcMetaAware {
    
    private RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();
    private final AbstractRpcHandler delegator;

    /**
     * Set the RemoteInvocationExecutor to use for this exporter.
     * Default is a DefaultRemoteInvocationExecutor.
     * <p>A custom invocation executor can extract further context information
     * from the invocation, for example user credentials.
     */
    public void setRemoteInvocationExecutor(RemoteInvocationExecutor remoteInvocationExecutor) {
        this.remoteInvocationExecutor = remoteInvocationExecutor;
    }

    /**
     * Return the RemoteInvocationExecutor used by this exporter.
     */
    public RemoteInvocationExecutor getRemoteInvocationExecutor() {
        return this.remoteInvocationExecutor;
    }
    
    /**
     * default constructor with {@link RpcHandler}
     */
    public RemoteExcuteInvokeRpcHandler(AbstractRpcHandler rpcHandler) {
        this.delegator = rpcHandler;
    }


    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#doHandle(com.baidu.jprotobuf.pbrpc.server.RpcData)
     */
    @Override
    public RpcData doHandle(RpcData data) throws Exception {
        
        RemoteInvocationProxy ri = new RemoteInvocationProxy(delegator, data);
        ri.setMethodName(delegator.getMethodName());
        byte[] extraParams = data.getExtraParams();
        if (extraParams != null) {
            Map unmarshal = (Map) SerializationUtils.deserialize(extraParams);
            ri.setAttributes(unmarshal);
        }
        
        return (RpcData) remoteInvocationExecutor.invoke(ri, delegator.getService());
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getServiceName()
     */
    @Override
    public String getServiceName() {
        return delegator.getServiceName();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getMethodName()
     */
    @Override
    public String getMethodName() {
        return delegator.getMethodName();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getService()
     */
    @Override
    public Object getService() {
        return delegator.getService();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware#getInputMetaProto()
     */
    @Override
    public String getInputMetaProto() {
        return delegator.getInputMetaProto();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware#getOutputMetaProto()
     */
    @Override
    public String getOutputMetaProto() {
        return delegator.getOutputMetaProto();
    }

}
