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

package com.baidu.jprotobuf.pbrpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Proxy factory by JDK default implements.
 * 
 * @author xiemalin
 * @since 1.0
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ProxyFactory {

    /**
     * To create proxy object. 
     *
     * @param <T> the generic type
     * @param clazz the clazz
     * @param classLoader the class loader
     * @param handler invocation handler
     * @return proxied object.
     */
    public static <T> T createProxy(Class[] clazz, ClassLoader classLoader, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(classLoader, clazz, handler);
    }
}
