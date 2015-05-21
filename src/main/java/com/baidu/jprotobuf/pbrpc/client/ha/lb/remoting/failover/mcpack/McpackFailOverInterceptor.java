/**
 * 
 */
package com.baidu.bjf.lb.remoting.failover.mcpack;

import java.lang.reflect.Method;

import com.baidu.bjf.lb.remoting.failover.support.UrlBaseFailOverInterceptor;

/**
 * Mcpack Json RPC load balance failover intercepter implementation.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class McpackFailOverInterceptor extends UrlBaseFailOverInterceptor {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.failover.FailOverInterceptor#isAvailable(java
     * .lang.Object, java.lang.reflect.Method, java.lang.String)
     */
    public boolean isAvailable(Object o, Method m, String beanKey) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.failover.FailOverInterceptor#isDoFailover(
     * java.lang.Throwable, java.lang.String)
     */
    public boolean isDoFailover(Throwable t, String beanKey) {
        return true;
    }


}
