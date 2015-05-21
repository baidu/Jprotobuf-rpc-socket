/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.client.ha.lb.failover;

import java.util.Map;

import org.apache.log4j.Logger;

import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean.FactoryBeanInvokeInfo;

/**
 * A load balance target bean recover heart beat thread.
 * 
 * @author xiemalin
 * @see Runnable
 * @since 2.16
 */
public class RecoverHeartbeat implements Runnable {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(RecoverHeartbeat.class);

    private LoadBalanceProxyFactoryBean proxyFactoryBean;

    private static final long DEFAULT_RECOVER_INTERVAL = 1000L;

    private static final long TOO_FREQUENT = 100L;

    private boolean runing;

    private boolean close = false;

    /**
     * get heart bean recover interval. unit 
     * @return
     */
    private long getRecoverInterval() {
        long recoverInterval = proxyFactoryBean.getRecoverInterval();
        if (recoverInterval <= TOO_FREQUENT) {
            LOGGER.warn("failover recover interval " + recoverInterval
                    + " is too frequent, using default:"
                    + DEFAULT_RECOVER_INTERVAL);
            recoverInterval = DEFAULT_RECOVER_INTERVAL;
        }

        return recoverInterval;
    }

    /**
     * @param proxyFactoryBean
     * @param recoverInterval
     */
    public RecoverHeartbeat(LoadBalanceProxyFactoryBean proxyFactoryBean) {
        this.proxyFactoryBean = proxyFactoryBean;
    }

    /**
     * @return thread running status
     */
    public boolean isRuning() {
        return runing;
    }

    /**
     * stop thread and exit
     */
    public void close() {
        close = true;
    }

    /**
     * main entry for thread running.
     */
    public void run() {
        runing = true;
        LOGGER.info("start recover heart beat thread");
        Map<String, FactoryBeanInvokeInfo> failedBeans;
        FailOverInterceptor interceptor = proxyFactoryBean
                .getFailOverInterceptor();
        while (proxyFactoryBean.hasFactoryBeanFailed() && !close) {
            failedBeans = proxyFactoryBean.getFailedFactoryBeans();
            if (failedBeans == null || failedBeans.isEmpty()) {
                return;
            }

            FactoryBeanInvokeInfo invokeInfo;
            for (Map.Entry<String, FactoryBeanInvokeInfo> entry : failedBeans
                    .entrySet()) {

                invokeInfo = entry.getValue();
                boolean available;
                try {
                    available = interceptor
                            .isRecover(invokeInfo.getBean(), invokeInfo
                                    .getInvocation(), invokeInfo.getBeanKey());
                } catch (Exception e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(e);
                    }
                    available = false;
                }
                if (available) {
                    proxyFactoryBean.recoverFactoryBean(entry.getKey());
                    LOGGER.info(invokeInfo.getBeanKey()
                            + " recover heart beat test success!");
                } else {
                    LOGGER.warn(invokeInfo.getBeanKey()
                            + " recover heart beat test failed!");
                }
            }
            // sleep here
            try {
                Thread.sleep(getRecoverInterval());
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(e);
                }
            }
        }
        runing = false;
        LOGGER
                .info("all heart beat test success. recover heart beat thread exit");
    }

}
