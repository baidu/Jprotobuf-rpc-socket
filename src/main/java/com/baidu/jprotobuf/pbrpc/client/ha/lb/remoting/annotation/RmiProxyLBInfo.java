/**
 * 
 */
package com.baidu.bjf.lb.remoting.annotation;

import com.baidu.bjf.remoting.rmi.annotation.RmiProxy;
import com.baidu.bjf.remoting.rmi.annotation.RmiProxyInfo;

/**
 * @author xiemalin
 * @since 1.0.0.0
 */
public class RmiProxyLBInfo {

    private RmiProxyInfo[] value;
    private String loadBalanceStrategyBeanName;
    private String failOverInterceptorBeanName;
    private long recoverInterval;
    private String failOverEventBeanName;
    
    
    public void setValue(RmiProxyInfo[] value) {
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

    /**
     * @return {@link RmiProxy} targets
     */
    public RmiProxyInfo[] value() {
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
