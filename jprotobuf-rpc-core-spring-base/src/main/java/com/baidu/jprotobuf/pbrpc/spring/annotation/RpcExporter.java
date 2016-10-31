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
package com.baidu.jprotobuf.pbrpc.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.spring.RpcServiceExporter;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;

/**
 * Annotation publish for {@link RpcServiceExporter}.
 *
 * @author xiemalin
 * @since 2.17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcExporter {

    /**
     * RPC server port to publish.
     *
     * @return the string
     */
    String port();
    
    /**
     * Host.
     *
     * @return the string
     */
    String host() default "";
    
    /**
     * bean name of RPC server options bean type must be {@link RpcServerOptions}.
     *
     * @return the string
     */
    String rpcServerOptionsBeanName() default "";
    
    /**
     * bean name of RPC intercepter bean type must be {@link InvokerInterceptor}.
     *
     * @return the string
     */
    String invokerIntercepterBeanName() default "";
    
}
