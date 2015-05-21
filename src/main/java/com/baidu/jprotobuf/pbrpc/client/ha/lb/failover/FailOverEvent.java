/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.client.ha.lb.failover;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Load balance fail over event interface.
 * 
 * @author xiemalin
 * @since 2.16
 */
public interface FailOverEvent {
    /**
     * if detect target is recovered
     * 
     * @param targetName
     */
    void onTargetRecover(String targetName);

    /**
     * if detect target is fail on specified invoke action
     * 
     * @param targetName
     * @param bean
     * @param invocation
     */
    void onTargetFailed(String targetName, Object bean,
            MethodInvocation invocation);
}
