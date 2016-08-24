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
package com.baidu.jprotobuf.pbrpc.spring;

import java.util.Map;

import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.SerializationUtils;

import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware;
import com.baidu.jprotobuf.pbrpc.server.AbstractAnnotationRpcHandler;
import com.baidu.jprotobuf.pbrpc.server.RpcData;


/**
 * {@link RemoteExcuteInvokeRpcHandler}.
 *
 * @author xiemalin
 * @since 2.17
 */
public class RemoteExcuteInvokeRpcHandler implements RpcHandler, RpcMetaAware {
    
    /** The remote invocation executor. */
    private RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();
    
    /** The delegator. */
    private final AbstractAnnotationRpcHandler delegator;

    /**
     * Sets the remote invocation executor.
     *
     * @param remoteInvocationExecutor the new remote invocation executor
     */
    public void setRemoteInvocationExecutor(RemoteInvocationExecutor remoteInvocationExecutor) {
        this.remoteInvocationExecutor = remoteInvocationExecutor;
    }

    /**
     * Gets the remote invocation executor.
     *
     * @return the remote invocation executor
     */
    public RemoteInvocationExecutor getRemoteInvocationExecutor() {
        return this.remoteInvocationExecutor;
    }
    
    /**
     * default constructor with {@link RpcHandler}.
     *
     * @param rpcHandler the rpc handler
     */
    public RemoteExcuteInvokeRpcHandler(AbstractAnnotationRpcHandler rpcHandler) {
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

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getInputClass()
     */
    @Override
    public Class<?> getInputClass() {
        return delegator.getInputClass();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getOutputClass()
     */
    @Override
    public Class<?> getOutputClass() {
        return delegator.getOutputClass();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getDescription()
     */
    @Override
    public String getDescription() {
        return delegator.getDescription();
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.RpcHandler#getMethodSignature(java.lang.String, java.lang.String)
     */
    @Override
    public String getMethodSignature() {
        return delegator.getMethodSignature();
    }

}
