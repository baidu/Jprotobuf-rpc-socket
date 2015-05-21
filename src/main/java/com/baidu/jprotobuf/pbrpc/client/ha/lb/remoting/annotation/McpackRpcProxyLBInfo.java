/**
 * 
 */
package com.baidu.bjf.lb.remoting.annotation;

import com.baidu.bjf.remoting.mcpack.annotation.McpackRpcProxyInfo;

/**
 * @author xiemalin
 * @since 1.0.0.0
 */
public class McpackRpcProxyLBInfo {
    
    private McpackRpcProxyInfo[] value;
    private String loadBalanceStrategyBeanName;
    private String failOverInterceptorBeanName;
    private long recoverInterval;
    private String failOverEventBeanName;
    
    public void setValue(McpackRpcProxyInfo[] value) {
        this.value = value;
    }

    public void setLoadBalanceStrategyBeanName(String loadBalanceStrategyBeanName) {
        this.loadBalanceStrategyBeanName = loadBalanceStrategyBeanName;
    }

    public void setFailOverInterceptorBeanName(String failOverInterceptorBeanName) {
        this.failOverInterceptorBeanName = failOverInterceptorBeanName;
    }

    public void setRecoverInterval(long recoverInterval) {
        this.recoverInterval = recoverInterval;
    }

    public void setFailOverEventBeanName(String failOverEventBeanName) {
        this.failOverEventBeanName = failOverEventBeanName;
    }

    public McpackRpcProxyInfo[] value() {
        return value;
    }
    
    /**
     * @return set from spring ioc container. if this set property
     *         'loadBalanceStrategy' will disabled
     */
    public String loadBalanceStrategyBeanName() {
        return loadBalanceStrategyBeanName;
    }

    /**
     * @return set from spring ioc container. if this set property
     *         'failOverInterceptor' will disabled
     */
    public String failOverInterceptorBeanName() {
        return failOverInterceptorBeanName;
    }

    /**
     * @return
     */
    public long recoverInterval() {
        return recoverInterval;
    }

    /**
     * @return
     */
    public String failOverEventBeanName() {
        return failOverEventBeanName;
    }
}
