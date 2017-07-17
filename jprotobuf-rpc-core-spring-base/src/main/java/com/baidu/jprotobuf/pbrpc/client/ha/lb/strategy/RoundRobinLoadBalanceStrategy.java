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
package com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;

/**
 * A weighted round robin strategy implementation for {@link LoadBalanceStrategy} interface.
 *
 * @author xiemalin
 * @see LoadBalanceStrategy
 * @see RoundRobinLoadBalanceStrategy
 * @since 2.16
 */
public class RoundRobinLoadBalanceStrategy implements NamingServiceLoadBalanceStrategy {

    /** The Constant MIN_LB_FACTOR. */
    private static final int MIN_LB_FACTOR = 1;

    /** The targets. */
    private List<String> targets;
    
    /** The current pos. */
    private int currentPos;

    /** The current targets. */
    private Map<String, Integer> currentTargets;
    
    /** The failed targets. */
    private Map<String, Integer> failedTargets;

    /** The naming service. */
    private NamingService namingService;

    /** defalut load factor for {@link RoundRobinLoadBalanceStrategy}. */
    private static final int DEFAULT_LOAD_FACTOR = 1;

    /** The service signature. */
    private String serviceSignature;

    /**
     * Gets the naming service.
     *
     * @return the naming service
     */
    public NamingService getNamingService() {
        return namingService;
    }

    /**
     * Constructor with load balance factors.
     *
     * @param serviceSignature the service signature
     * @param namingService the naming service
     */
    public RoundRobinLoadBalanceStrategy(String serviceSignature, NamingService namingService) {

        this.serviceSignature = serviceSignature;
        this.namingService = namingService;
        doReInit(this.serviceSignature, namingService);
    }

    /**
     * Instantiates a new round robin load balance strategy.
     *
     * @param lbFactors the lb factors
     */
    public RoundRobinLoadBalanceStrategy(Map<String, Integer> lbFactors) {
        init(lbFactors);
    }

    /**
     * Inits the.
     *
     * @param servers the servers
     */
    protected void init(List<RegisterInfo> servers) {
        Map<String, Integer> lbFactors = parseLbFactors(servers);
        init(lbFactors);
    }

    /**
     * Parses the lb factors.
     *
     * @param servers the servers
     * @return the map
     */
    private Map<String, Integer> parseLbFactors(List<RegisterInfo> servers) {
        Map<String, Integer> lbFactors = new HashMap<String, Integer>();
        if (servers == null) {
            return lbFactors;
        }
        
        for (RegisterInfo address : servers) {
            String serviceUrl = address.getHost() + ":" + address.getPort();
            lbFactors.put(serviceUrl, DEFAULT_LOAD_FACTOR);
        }
        return lbFactors;
    }

    /**
     * Inits the.
     *
     * @param lbFactors the lb factors
     */
    protected synchronized void init(Map<String, Integer> lbFactors) {
        currentTargets = Collections.synchronizedMap(lbFactors);
        failedTargets = Collections.synchronizedMap(new HashMap<String, Integer>(currentTargets.size()));
        reInitTargets(currentTargets);
    }

    /**
     * Re init targets.
     *
     * @param lbFactors the lb factors
     */
    private void reInitTargets(Map<String, Integer> lbFactors) {
        targets = initTargets(lbFactors);
        currentPos = 0;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy#elect()
     */
    public synchronized String elect() {
        if (targets == null || targets.isEmpty()) {
            throw new RuntimeException("no target is available");
        }

        if (currentPos >= targets.size()) {
            currentPos = 0;
        }
        return targets.get(currentPos++);
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy#getTargets()
     */
    public synchronized Set<String> getTargets() {
        if (targets == null) {
            return new HashSet<String>(0);
        }
        return new HashSet<String>(targets);
    }

    /**
     * Initialize the targets and set weighted value for each target.
     *
     * @param lbFactors the lb factors
     * @return the list
     */
    public List<String> initTargets(Map<String, Integer> lbFactors) {
        if (lbFactors == null || lbFactors.size() == 0) {
            return null;
        }
        if (lbFactors.size() == 1) { // only one target
            return new ArrayList<String>(lbFactors.keySet());
        }

        fixFactor(lbFactors);

        Collection<Integer> factors = lbFactors.values();
        // get min factor
        int min = Collections.min(factors);
        if (min > MIN_LB_FACTOR) {
            List<Integer> divisors = getDivisors(min);
            int maxDivisor = getMaxDivisor(divisors, factors);
            return buildBalanceTargets(lbFactors, maxDivisor);
        }
        return buildBalanceTargets(lbFactors, MIN_LB_FACTOR);
    }

    /**
     * Gets the max divisor.
     *
     * @param divisors the divisors
     * @param factors the factors
     * @return the max divisor
     */
    private int getMaxDivisor(List<Integer> divisors, Collection<Integer> factors) {
        for (Integer divisor : divisors) {
            if (canModAll(divisor, factors)) {
                return divisor;
            }
        }
        return 1;
    }

    /**
     * Gets the divisors.
     *
     * @param value the value
     * @return the divisors
     */
    private List<Integer> getDivisors(int value) {
        if (value <= MIN_LB_FACTOR) {
            return Collections.emptyList();
        }
        int count = value / 2;
        List<Integer> divisors = new ArrayList<Integer>(count + 1);
        divisors.add(value);
        for (; count > 0; count--) {
            if (value % count == 0) {
                divisors.add(count);
            }
        }
        return divisors;
    }

    /**
     * lb factor must great than 0.
     *
     * @param lbFactors the lb factors
     */
    private void fixFactor(Map<String, Integer> lbFactors) {
        Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
        for (Map.Entry<String, Integer> entry : setEntries) {
            if (entry.getValue() < MIN_LB_FACTOR) {
                entry.setValue(MIN_LB_FACTOR);
            }
        }
    }

    /**
     * Can do modulo operation for all factor by base value.
     *
     * @param base the base
     * @param factors the factors
     * @return true, if successful
     */
    private boolean canModAll(int base, Collection<Integer> factors) {
        for (Integer integer : factors) {
            if (integer % base != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Builds the balance targets.
     *
     * @param lbFactors the lb factors
     * @param baseFactor the base factor
     * @return the list
     */
    private List<String> buildBalanceTargets(Map<String, Integer> lbFactors, int baseFactor) {
        Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
        int factor;
        List<String> targets = new LinkedList<String>();
        for (Map.Entry<String, Integer> entry : setEntries) {
            factor = entry.getValue() / baseFactor;

            for (int i = 0; i < factor; i++) {
                targets.add(entry.getKey());
            }
        }
        return targets;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy#removeTarget(java.lang.String)
     */
    public synchronized void removeTarget(String key) {
        if (currentTargets.containsKey(key)) {
            failedTargets.put(key, currentTargets.get(key));
            currentTargets.remove(key);
            reInitTargets(currentTargets);
        }
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy#recoverTarget(java.lang.String)
     */
    public synchronized void recoverTarget(String key) {
        if (failedTargets.containsKey(key)) {
            currentTargets.put(key, failedTargets.get(key));
            failedTargets.remove(key);
            reInitTargets(currentTargets);
        }
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy#hasTargets()
     */
    public boolean hasTargets() {
        return (getTargets() != null && !getTargets().isEmpty());
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy#getFailedTargets()
     */
    public Set<String> getFailedTargets() {
        return failedTargets.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategy#doReInit(com.baidu.jprotobuf
     * .pbrpc.client.ha.NamingService)
     */
    @Override
    public void doReInit(String serviceSignagure, NamingService namingService) {
        // get server list from NamingService
        
        Set<String> serviceSignatures = new HashSet<String>();
        serviceSignatures.add(serviceSignagure);
        
        List<RegisterInfo> servers;
        try {
            servers = namingService.list(serviceSignatures).get(serviceSignagure);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        init(servers);

    }

}
