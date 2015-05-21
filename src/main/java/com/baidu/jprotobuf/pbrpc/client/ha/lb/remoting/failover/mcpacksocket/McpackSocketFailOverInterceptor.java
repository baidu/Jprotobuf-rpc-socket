package com.baidu.bjf.lb.remoting.failover.mcpacksocket;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.bjf.lb.failover.FailOverInterceptor;
import com.baidu.bjf.remoting.mcpacksocket.io.support.Client;

/**
 * Mcpack Socket load balance failover intercepter implementation.
 * 
 * @author lijianbin
 * @since 1.0.4.0
 * @see FailOverInterceptor
 * @see LoadBalanceProxyFactoryBean
 */
public class McpackSocketFailOverInterceptor implements FailOverInterceptor {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(McpackSocketFailOverInterceptor.class);
    
    /**
     * Client map, key: unique proxy name, value: mcpack Client
     */
    private Map<String, Client> clients;
    
    /**
     * setting the mcpack clients map
     * 
     * @param clients
     *            clients' map
     */
    public void setMcpackClients(Map<String, Client> clients) {
        this.clients = clients;
    }
    
    /**
     * clear all the mcpack clients
     */
    public void clearClients() {
        if (clients != null) {
            clients.clear();
        }
    }
    
    /**
     * return if target is available.
     * 
     * @param obj
     *            object
     * @param method
     *            method
     * @param beanKey
     *            bean key
     * @return true if available
     */
    public boolean isAvailable(Object obj, Method method, String beanKey) {
        return true;
    }
    
    /**
     * return if failed target is recovered
     * 
     * @param obj
     *            object
     * @param method
     *            method
     * @param beanKey
     *            bean key
     * @return true if recovered
     */
    public boolean isRecover(Object obj, Method method, String beanKey) {
        if (clients == null) {
            LOGGER.error("No client is set!");
            return false;
        }
        
        Client client = clients.get(beanKey);
        
        if (client == null) {
            LOGGER.error("can not find client for: " + beanKey);
            return false;
        }
        
        try {
            // send en empty packet to test recovering
            client.send(null, Object.class);
            LOGGER.info(beanKey + " is recovered!");
            return true;
        } catch (Exception e) {
            LOGGER.warn(beanKey + " not recovered.", e);
        }
        
        return false;
    }
    
    /**
     * return is catch exception need do fail over action
     * 
     * @param t
     *            exception to check
     * @param beanKey
     *            bean key
     * @return true if do fail over action
     */
    public boolean isDoFailover(Throwable t, String beanKey) {
        // always do failover
        return true;
    }
    
}
