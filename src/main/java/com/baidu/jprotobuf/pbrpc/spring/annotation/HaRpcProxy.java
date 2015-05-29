/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.spring.HaProtobufRpcProxyBean;

/**
 * Annotation publish for {@link HaProtobufRpcProxyBean}
 *
 * @author xiemalin
 * @since 2.17
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface HaRpcProxy {
    
    /**
     * bean name of {@link NamingService}
     */
    String namingServiceBeanName();
    
    /**
     * Set the interface of the service to access. The interface must be suitable 
     * for the particular service and remoting strategy.
     *
     * Typically required to be able to create a suitable service proxy, 
     * but can also be optional if the lookup returns a typed proxy. 
     */
    Class<?> serviceInterface();
    

    /**
     * bean name of RPC client options
     */
    String rpcClientOptionsBeanName() default "";
    
    /**
     * try to connect to server on startup
     */
    boolean lookupStubOnStartup() default true;
}
