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

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * Rpc service description info.
 *
 * @author xiemalin
 * @since 2.1
 */
public class RpcServiceMeta {

    /** rpc 服务名. */
    @Protobuf(required = true)
    private String serviceName;
    
    /** rpc方法中. */
    @Protobuf(required = true)
    private String methodName;
    
    /** 请求参数 google protocol buffer IDL 描述说明. */
    @Protobuf
    private String inputProto;
    
    /** 请求参数对象名称. */
    @Protobuf 
    private String inputObjName;
    
    /**  返回参数 google protocol buffer IDL 描述说明. */
    @Protobuf
    private String outputProto;
    
    /** 返回参数对象名称. */
    @Protobuf 
    private String outputObjName;

    /**
     * Gets the rpc 服务名.
     *
     * @return the rpc 服务名
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the rpc 服务名.
     *
     * @param serviceName the new rpc 服务名
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Gets the rpc方法中.
     *
     * @return the rpc方法中
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the rpc方法中.
     *
     * @param methodName the new rpc方法中
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the 请求参数 google protocol buffer IDL 描述说明.
     *
     * @return the 请求参数 google protocol buffer IDL 描述说明
     */
    public String getInputProto() {
        return inputProto;
    }

    /**
     * Sets the 请求参数 google protocol buffer IDL 描述说明.
     *
     * @param inputProto the new 请求参数 google protocol buffer IDL 描述说明
     */
    public void setInputProto(String inputProto) {
        this.inputProto = inputProto;
    }

    /**
     * Gets the 返回参数 google protocol buffer IDL 描述说明.
     *
     * @return the 返回参数 google protocol buffer IDL 描述说明
     */
    public String getOutputProto() {
        return outputProto;
    }

    /**
     * Sets the 返回参数 google protocol buffer IDL 描述说明.
     *
     * @param outputProto the new 返回参数 google protocol buffer IDL 描述说明
     */
    public void setOutputProto(String outputProto) {
        this.outputProto = outputProto;
    }

    /**
     * Gets the 请求参数对象名称.
     *
     * @return the 请求参数对象名称
     */
    public String getInputObjName() {
        return inputObjName;
    }

    /**
     * Sets the 请求参数对象名称.
     *
     * @param inputObjName the new 请求参数对象名称
     */
    public void setInputObjName(String inputObjName) {
        this.inputObjName = inputObjName;
    }

    /**
     * Gets the 返回参数对象名称.
     *
     * @return the 返回参数对象名称
     */
    public String getOutputObjName() {
        return outputObjName;
    }

    /**
     * Sets the 返回参数对象名称.
     *
     * @param outputObjName the new 返回参数对象名称
     */
    public void setOutputObjName(String outputObjName) {
        this.outputObjName = outputObjName;
    }
    
    
}
