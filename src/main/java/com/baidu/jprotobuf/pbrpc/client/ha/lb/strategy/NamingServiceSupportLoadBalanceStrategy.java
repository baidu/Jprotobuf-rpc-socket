/*
 * Copyright 2002-2014 the original author or authors.
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
package com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;

/**
 * 
 * Abstract {@link LoadBalanceStrategy} which support {@link NamingService} list result refresh support
 * 
 * @author xiemalin
 * @since 2.16
 */
public abstract class NamingServiceSupportLoadBalanceStrategy implements LoadBalanceStrategy {

    private static final Logger LOG = Logger.getLogger(NamingServiceSupportLoadBalanceStrategy.class.getName());

    private Timer timer;
    private TimerTask updateListTask;
    
    /**
     * delay in milliseconds before NamingService result refresh update task is to be executed.
     */
    private long delay = 1000;
    
    /**
     *  time in milliseconds between successive NamingService result refresh update task executions.
     */
    private long period = 1000;

    public abstract NamingService getNamingService();

    /**
     * set delay value to delay
     * 
     * @param delay the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * set period value to period
     * 
     * @param period the period to set
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    protected abstract void reInit(List<InetSocketAddress> list);

    protected void startUpdateNamingServiceTask(List<InetSocketAddress> list) {
        if (getNamingService() == null) {
            return;
        }

        this.timer = new Timer(true);
        updateListTask = new UpdateNamingServiceTask(this, list);
        this.timer.scheduleAtFixedRate(updateListTask, delay, period);
    }

    public void close() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private final class UpdateNamingServiceTask extends TimerTask {
        NamingServiceSupportLoadBalanceStrategy loadBalancer;
        private List<InetSocketAddress> list;

        public UpdateNamingServiceTask(NamingServiceSupportLoadBalanceStrategy loadBalancer,
                List<InetSocketAddress> list) {
            this.loadBalancer = loadBalancer;
            this.list = new ArrayList<InetSocketAddress>(list);
        }

        @Override
        public void run() {
            try {
                List<InetSocketAddress> serverList = loadBalancer.getNamingService().list();
                if (serverList == null) {
                    serverList = Collections.emptyList();
                } 
                
                // to check changes
                if (list.equals(serverList)) {
                    return;
                }
                list = new ArrayList<InetSocketAddress>(serverList);
                reInit(serverList);
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage(), e.getCause());
            }
        }
    }
}
