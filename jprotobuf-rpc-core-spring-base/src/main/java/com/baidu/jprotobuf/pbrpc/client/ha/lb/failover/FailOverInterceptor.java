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
package com.baidu.jprotobuf.pbrpc.client.ha.lb.failover;

import java.lang.reflect.Method;

/**
 * Load balance fail over intercepter interface.<br>
 * 
 * @author xiemalin
 * @see LoadBalanceProxyFactoryBean
 * @since 2.16
 */
public interface FailOverInterceptor {
    
    /**
     * return if target is available.
     *
     * @param o the o
     * @param m the m
     * @param beanKey the bean key
     * @return true if available
     */
    boolean isAvailable(Object o, Method m, String beanKey);

    /**
     * return if failed target is recovered.
     *
     * @param o the o
     * @param m the m
     * @param beanKey the bean key
     * @return true if recovered
     */
    boolean isRecover(Object o, Method m, String beanKey);

    /**
     * return is catch exception need do fail over action.
     *
     * @param t the t
     * @param beanKey the bean key
     * @return true if do fail over action
     */
    boolean isDoFailover(Throwable t, String beanKey);
}
