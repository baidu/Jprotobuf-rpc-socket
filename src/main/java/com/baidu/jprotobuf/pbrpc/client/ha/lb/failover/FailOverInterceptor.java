/**
 * 
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
     * @param o
     * @param m
     * @param beanKey
     * @return true if available
     */
    boolean isAvailable(Object o, Method m, String beanKey);

    /**
     * return if failed target is recovered
     * 
     * @param o
     * @param m
     * @param beanKey
     * @return true if recovered
     */
    boolean isRecover(Object o, Method m, String beanKey);

    /**
     * return is catch exception need do fail over action
     * 
     * @param t
     * @param beanKey
     * @return true if do fail over action
     */
    boolean isDoFailover(Throwable t, String beanKey);
}
