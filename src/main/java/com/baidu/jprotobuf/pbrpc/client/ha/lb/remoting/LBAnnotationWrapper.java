/**
 * 
 */
package com.baidu.bjf.lb.remoting;

import org.apache.commons.lang.ArrayUtils;

import com.baidu.bjf.lb.remoting.annotation.McpackRpcProxyLBInfo;
import com.baidu.bjf.lb.remoting.annotation.McpackRpcProxyLB;
import com.baidu.bjf.lb.remoting.annotation.RmiProxyLB;
import com.baidu.bjf.lb.remoting.annotation.RmiProxyLBInfo;
import com.baidu.bjf.remoting.mcpack.annotation.McpackRpcProxy;
import com.baidu.bjf.remoting.mcpack.annotation.McpackRpcProxyInfo;
import com.baidu.bjf.remoting.rmi.annotation.RmiProxy;
import com.baidu.bjf.remoting.rmi.annotation.RmiProxyInfo;
import com.baidu.bjf.remoting.utils.AnnotationWrapper;

/**
 * @author xiemalin
 * @since 1.0.0.0
 */
public class LBAnnotationWrapper {

    public static RmiProxyLBInfo wrapRmiProxyLB(RmiProxyLB rmiProxyLB) {
        if (rmiProxyLB == null) {
            return null;
        }
        RmiProxyLBInfo info = new RmiProxyLBInfo();
        RmiProxy[] value = rmiProxyLB.value();
        if (!ArrayUtils.isEmpty(value)) {
            RmiProxyInfo[] rmiProxies = new RmiProxyInfo[value.length];
            for (int i = 0; i < value.length; i++) {
                rmiProxies[i] = AnnotationWrapper.wrapRmiProxy(value[i]);
            }
            info.setValue(rmiProxies);
        }
        info.setFailOverEventBeanName(rmiProxyLB.failOverEventBeanName());
        info.setFailOverInterceptorBeanName(rmiProxyLB.failOverInterceptorBeanName());
        info.setLoadBalanceStrategyBeanName(rmiProxyLB.loadBalanceStrategyBeanName());
        info.setRecoverInterval(rmiProxyLB.recoverInterval());
        return info;
    }
    
    public static McpackRpcProxyLBInfo wrapMcpackRpcProxyLB(McpackRpcProxyLB mcpackRpcProxyLB) {
        if (mcpackRpcProxyLB == null) {
            return null;
        }
        McpackRpcProxyLBInfo info = new McpackRpcProxyLBInfo();
        McpackRpcProxy[] value = mcpackRpcProxyLB.value();
        if (!ArrayUtils.isEmpty(value)) {
            McpackRpcProxyInfo[] proxies = new McpackRpcProxyInfo[value.length];
            for (int i = 0; i < value.length; i++) {
                proxies[i] = AnnotationWrapper.wrapMcpackRpcProxy(value[i]);
            }
            info.setValue(proxies);
        }
        info.setFailOverEventBeanName(mcpackRpcProxyLB.failOverEventBeanName());
        info.setFailOverInterceptorBeanName(mcpackRpcProxyLB.failOverInterceptorBeanName());
        info.setLoadBalanceStrategyBeanName(mcpackRpcProxyLB.loadBalanceStrategyBeanName());
        info.setRecoverInterval(mcpackRpcProxyLB.recoverInterval());
        
        return info;
    }
}
