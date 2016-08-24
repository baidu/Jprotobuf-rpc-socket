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

package com.baidu.jprotobuf.pbrpc.meta;

import java.util.List;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * {@link List} collection of {@link RpcServiceMeta}.
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcServiceMetaList {

    /** The rpc service metas. */
    @Protobuf(fieldType = FieldType.OBJECT)
    private List<RpcServiceMeta> rpcServiceMetas;
    
    /** The types idl. */
    @Protobuf
    private String typesIDL;
    
    /** The rpcs idl. */
    @Protobuf
    private String rpcsIDL;
    

    /**
     * Gets the rpc service metas.
     *
     * @return the rpc service metas
     */
    public List<RpcServiceMeta> getRpcServiceMetas() {
        return rpcServiceMetas;
    }

    /**
     * Sets the rpc service metas.
     *
     * @param rpcServiceMetas the new rpc service metas
     */
    public void setRpcServiceMetas(List<RpcServiceMeta> rpcServiceMetas) {
        this.rpcServiceMetas = rpcServiceMetas;
    }

    /**
     * Gets the types idl.
     *
     * @return the types idl
     */
    public String getTypesIDL() {
        return typesIDL;
    }

    /**
     * Sets the types idl.
     *
     * @param typesIDL the new types idl
     */
    public void setTypesIDL(String typesIDL) {
        this.typesIDL = typesIDL;
    }

    /**
     * Gets the rpcs idl.
     *
     * @return the rpcs idl
     */
    public String getRpcsIDL() {
        return rpcsIDL;
    }

    /**
     * Sets the rpcs idl.
     *
     * @param rpcsIDL the new rpcs idl
     */
    public void setRpcsIDL(String rpcsIDL) {
        this.rpcsIDL = rpcsIDL;
    }
    
    
}
