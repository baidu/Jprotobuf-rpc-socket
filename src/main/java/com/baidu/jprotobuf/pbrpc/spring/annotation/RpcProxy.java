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
package com.baidu.jprotobuf.pbrpc.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baidu.jprotobuf.pbrpc.spring.ProtobufRpcProxyBean;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;

/**
 * Annotation publish for {@link ProtobufRpcProxyBean}
 *
 * @author xiemalin
 * @since 2.17
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcProxy {

    /**
     * RPC server port to connect
     */
    String port();
    
    /**
     * Set the interface of the service to access. The interface must be suitable 
     * for the particular service and remoting strategy.
     *
     * Typically required to be able to create a suitable service proxy, 
     * but can also be optional if the lookup returns a typed proxy. 
     */
    Class<?> serviceInterface();
    
    /**
     * RPC server host to connect 
     */
    String host() default "127.0.0.1";
    
    /**
     * bean name of RPC client options.  bean type must be {@link RpcClientOptions}
     */
    String rpcClientOptionsBeanName() default "";
    
    /**
     * try to connect to server on startup
     */
    boolean lookupStubOnStartup() default true;
}
