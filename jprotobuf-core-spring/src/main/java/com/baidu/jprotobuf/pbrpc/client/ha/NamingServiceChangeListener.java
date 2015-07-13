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
package com.baidu.jprotobuf.pbrpc.client.ha;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;

/**
 * A listenr for {@link NamingService} changed call back.
 * 
 * @author xiemalin
 * @since 2.18
 */
public abstract class NamingServiceChangeListener {

    private static final Logger LOG = Logger.getLogger(NamingServiceChangeListener.class.getName());

    private Timer timer;
    private TimerTask updateListTask;

    /**
     * delay in milliseconds before NamingService result refresh update task is to be executed.
     */
    private long delay = 1000;

    /**
     * time in milliseconds between successive NamingService result refresh update task executions.
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

    protected abstract void reInit(String service, List<RegisterInfo> list) throws Exception;

    protected void startUpdateNamingServiceTask(Map<String, List<RegisterInfo>> serviceMap) {
        if (getNamingService() == null) {
            return;
        }

        this.timer = new Timer(true);
        updateListTask = new UpdateNamingServiceTask(this, serviceMap);
        this.timer.scheduleAtFixedRate(updateListTask, delay, period);
    }

    public void close() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private final class UpdateNamingServiceTask extends TimerTask {
        NamingServiceChangeListener loadBalancer;
        private Map<String, List<RegisterInfo>> serviceMap;

        public UpdateNamingServiceTask(NamingServiceChangeListener loadBalancer,
                Map<String, List<RegisterInfo>> serviceMap) {
            this.loadBalancer = loadBalancer;
            this.serviceMap = new HashMap<String, List<RegisterInfo>>();

            // copy map
            Iterator<Entry<String, List<RegisterInfo>>> iter = serviceMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, List<RegisterInfo>> entry = iter.next();
                
                List<RegisterInfo> list = entry.getValue();
                if (list == null) {
                    list = new ArrayList<RegisterInfo>();
                }
                
                this.serviceMap.put(entry.getKey(), new ArrayList<RegisterInfo>(list));
            }

        }

        @Override
        public void run() {
            try {

                Set<String> serviceNames = serviceMap.keySet();

                Map<String, List<RegisterInfo>> eServcieMap = loadBalancer.getNamingService().list(serviceNames);
                if (eServcieMap == null) {
                    eServcieMap = Collections.emptyMap();
                }

                Iterator<Entry<String, List<RegisterInfo>>> iter = serviceMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, List<RegisterInfo>> next = iter.next();
                    String service = next.getKey();
                    List<RegisterInfo> oldList = next.getValue();
                    if (oldList == null) {
                        oldList = Collections.emptyList();
                    }
                    List<RegisterInfo> newList = eServcieMap.get(service);
                    if (newList == null) {
                        newList = Collections.emptyList();
                    }

                    if (oldList.equals(newList)) {
                        continue;
                    }

                    LOG.log(Level.WARNING, "A new changed list geting from naming service name='" + service + "' "
                            + "value=" + newList);
                    List<RegisterInfo> list = new ArrayList<RegisterInfo>(newList);
                    next.setValue(list);
                    reInit(service, list);
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage(), e.getCause());
            }
        }
    }
}
