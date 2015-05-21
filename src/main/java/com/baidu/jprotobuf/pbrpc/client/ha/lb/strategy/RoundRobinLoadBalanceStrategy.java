/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;

/**
 * A weighted round robin strategy implementation for {@link LoadBalanceStrategy} interface
 * 
 * @author xiemalin
 * @see LoadBalanceStrategy
 * @see RoundRobinLoadBalanceStrategy
 * @since 2.16
 */
public class RoundRobinLoadBalanceStrategy extends NamingServiceSupportLoadBalanceStrategy {

    private static final int MIN_LB_FACTOR = 1;

    private List<String> targets;
    private int currentPos;

    private Map<String, Integer> currentTargets;
    private Map<String, Integer> failedTargets;

    private NamingService namingService;

    /**
     * get the namingService
     * 
     * @return the namingService
     */
    public NamingService getNamingService() {
        return namingService;
    }

    /**
     * defalut load factor for {@link RoundRobinLoadBalanceStrategy}
     */
    private static final int DEFAULT_LOAD_FACTOR = 1;

    /**
     * Constructor with load balance factors.
     * 
     * @param lbFactors
     */
    public RoundRobinLoadBalanceStrategy(NamingService namingService) {

        this.namingService = namingService;
        // get server list from NamingService
        List<InetSocketAddress> servers;
        try {
            servers = namingService.list();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (CollectionUtils.isEmpty(servers)) {
            throw new RuntimeException("Can not initialize due to get a blank server list from namingService.");
        }

        init(servers);
    }

    public RoundRobinLoadBalanceStrategy(Map<String, Integer> lbFactors) {
        init(lbFactors);
    }

    protected void init(List<InetSocketAddress> servers) {
        Map<String, Integer> lbFactors = parseLbFactors(servers);

        init(lbFactors);

        startUpdateNamingServiceTask(servers);
    }

    /**
     * @param servers
     * @return
     */
    private Map<String, Integer> parseLbFactors(List<InetSocketAddress> servers) {
        Map<String, Integer> lbFactors = new HashMap<String, Integer>();
        for (InetSocketAddress address : servers) {
            String serviceUrl = address.getHostName() + ":" + address.getPort();
            lbFactors.put(serviceUrl, DEFAULT_LOAD_FACTOR);
        }
        return lbFactors;
    }

    protected void init(Map<String, Integer> lbFactors) {
        currentTargets = Collections.synchronizedMap(lbFactors);
        failedTargets = Collections.synchronizedMap(new HashMap<String, Integer>(currentTargets.size()));
        reInitTargets(currentTargets);
    }

    private void reInitTargets(Map<String, Integer> lbFactors) {
        targets = initTargets(lbFactors);
        currentPos = 0;
    }

    public synchronized String elect() {
        if (targets == null) {
            throw new RuntimeException("no target is available");
        }

        if (currentPos >= targets.size()) {
            currentPos = 0;
        }
        return targets.get(currentPos++);
    }

    public synchronized Set<String> getTargets() {
        if (targets == null) {
            return new HashSet<String>(0);
        }
        return new HashSet<String>(targets);
    }

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

    private int getMaxDivisor(List<Integer> divisors, Collection<Integer> factors) {
        for (Integer divisor : divisors) {
            if (canModAll(divisor, factors)) {
                return divisor;
            }
        }
        return 1;
    }

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
     * lb factor must great than 0
     * 
     * @param lbFactor
     */
    private void fixFactor(Map<String, Integer> lbFactors) {
        Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
        for (Map.Entry<String, Integer> entry : setEntries) {
            if (entry.getValue() < MIN_LB_FACTOR) {
                entry.setValue(MIN_LB_FACTOR);
            }
        }
    }

    private boolean canModAll(int base, Collection<Integer> factors) {
        for (Integer integer : factors) {
            if (integer % base != 0) {
                return false;
            }
        }
        return true;
    }

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

    public synchronized void removeTarget(String key) {
        if (currentTargets.containsKey(key)) {
            failedTargets.put(key, currentTargets.get(key));
            currentTargets.remove(key);
            reInitTargets(currentTargets);
        }
    }

    public synchronized void recoverTarget(String key) {
        if (failedTargets.containsKey(key)) {
            currentTargets.put(key, failedTargets.get(key));
            failedTargets.remove(key);
            reInitTargets(currentTargets);
        }
    }

    public boolean hasTargets() {
        return !CollectionUtils.isEmpty(getTargets());
    }

    public Set<String> getFailedTargets() {
        return failedTargets.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceSupportLoadBalanceStrategy#reInit(java.util.List)
     */
    @Override
    protected void reInit(List<InetSocketAddress> list) {
        Map<String, Integer> lbFactors = parseLbFactors(list);
        reInitTargets(lbFactors);

    }
}
