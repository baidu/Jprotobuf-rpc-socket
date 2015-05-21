/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy;

import java.util.Set;

import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;

/**
 * load balance strategy interface
 * @see LoadBalanceProxyFactoryBean
 * @author xiemalin
 * @since 2.16
 */
public interface LoadBalanceStrategy {
    /**
     * do load balance and get target
     * @return
     */
    String elect();
	
	/**
	 * remove target from load balance target list.
	 * @param t
	 */
	void removeTarget(String t);
	
	/**
	 * add target to load balance target list.
	 * @param t
	 */
	void recoverTarget(String t);
	
	/**
	 * get availabled load balance target list. 
	 * @return
	 */
	Set<String> getTargets();
	
	/**
	 * @return true if availabled load balance target list is not empty
	 */
	boolean hasTargets();
	
	/** 
	 * @return failed load balance target
	 */
	Set<String> getFailedTargets();
	
	void close();
}
