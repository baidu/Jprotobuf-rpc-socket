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

package com.baidu.jprotobuf.pbrpc.meta;

import java.util.List;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * {@link List} collection of {@link RpcServiceMeta}
 *
 * @author xiemalin
 * @since 1.0
 */
public class RpcServiceMetaList {

    @Protobuf(fieldType = FieldType.OBJECT)
    private List<RpcServiceMeta> rpcServiceMetas;
    @Protobuf
    private String typesIDL;
    @Protobuf
    private String rpcsIDL;
    

    /**
     * get the rpcServiceMetas
     * @return the rpcServiceMetas
     */
    public List<RpcServiceMeta> getRpcServiceMetas() {
        return rpcServiceMetas;
    }

    /**
     * set rpcServiceMetas value to rpcServiceMetas
     * @param rpcServiceMetas the rpcServiceMetas to set
     */
    public void setRpcServiceMetas(List<RpcServiceMeta> rpcServiceMetas) {
        this.rpcServiceMetas = rpcServiceMetas;
    }

    /**
     * get the typesIDL
     * @return the typesIDL
     */
    public String getTypesIDL() {
        return typesIDL;
    }

    /**
     * set typesIDL value to typesIDL
     * @param typesIDL the typesIDL to set
     */
    public void setTypesIDL(String typesIDL) {
        this.typesIDL = typesIDL;
    }

    /**
     * get the rpcsIDL
     * @return the rpcsIDL
     */
    public String getRpcsIDL() {
        return rpcsIDL;
    }

    /**
     * set rpcsIDL value to rpcsIDL
     * @param rpcsIDL the rpcsIDL to set
     */
    public void setRpcsIDL(String rpcsIDL) {
        this.rpcsIDL = rpcsIDL;
    }
    
    
}
