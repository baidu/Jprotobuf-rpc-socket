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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(RecoverHeartbeat.class.getName());

    /** The proxy factory bean. */
    private LoadBalanceProxyFactoryBean proxyFactoryBean;

    /** The Constant DEFAULT_RECOVER_INTERVAL. */
    private static final long DEFAULT_RECOVER_INTERVAL = 1000L;

    /** The Constant TOO_FREQUENT. */
    private static final long TOO_FREQUENT = 100L;

    /** The runing. */
    private boolean runing;

    /** The close. */
    private boolean close = false;

    /**
     * Gets the recover interval.
     *
     * @return the recover interval
     */
    private long getRecoverInterval() {
        long recoverInterval = proxyFactoryBean.getRecoverInterval();
        if (recoverInterval <= TOO_FREQUENT) {
            LOGGER.log(Level.WARNING, "failover recover interval " + recoverInterval
                    + " is too frequent, using default:"
                    + DEFAULT_RECOVER_INTERVAL);
            recoverInterval = DEFAULT_RECOVER_INTERVAL;
        }

        return recoverInterval;
    }

    /**
     * Instantiates a new recover heartbeat.
     *
     * @param proxyFactoryBean the proxy factory bean
     */
    public RecoverHeartbeat(LoadBalanceProxyFactoryBean proxyFactoryBean) {
        this.proxyFactoryBean = proxyFactoryBean;
    }

    /**
     * Checks if is runing.
     *
     * @return true, if is runing
     */
    public boolean isRuning() {
        return runing;
    }

    /**
     * stop thread and exit.
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
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, e.getMessage());
                    }
                    available = false;
                }
                if (available) {
                    proxyFactoryBean.recoverFactoryBean(entry.getKey());
                    LOGGER.info(invokeInfo.getBeanKey()
                            + " recover heart beat test success!");
                } else {
                    LOGGER.log(Level.WARNING, invokeInfo.getBeanKey()
                            + " recover heart beat test failed!");
                }
            }
            // sleep here
            try {
                Thread.sleep(getRecoverInterval());
            } catch (Exception e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, e.getMessage());
                }
            }
        }
        runing = false;
        LOGGER.info("all heart beat test success. recover heart beat thread exit");
    }

}
