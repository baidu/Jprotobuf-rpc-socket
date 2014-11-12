/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Proxy factory by JDK default implements.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class ProxyFactory {

    /**
     * To create proxy object. 
     * 
     * @param <T> 
     * @param type proxy class type
     * @param handler invocation handler
     * @return proxied object.
     */
    public static <T> T createProxy(Class<T> type, InvocationHandler handler) {
        Class[] clazz = { type };
        return (T) Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), clazz, handler);
    }
}
