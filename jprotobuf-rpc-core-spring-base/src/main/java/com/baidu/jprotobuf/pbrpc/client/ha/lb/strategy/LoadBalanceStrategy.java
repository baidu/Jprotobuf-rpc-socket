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

import java.util.Set;

import com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean;

/**
 * load balance strategy interface.
 *
 * @author xiemalin
 * @see LoadBalanceProxyFactoryBean
 * @since 2.16
 */
public interface LoadBalanceStrategy {
    
    /**
     * do load balance and get target.
     *
     * @return the string
     */
    String elect();
	
	/**
	 * remove target from load balance target list.
	 *
	 * @param t the t
	 */
	void removeTarget(String t);
	
	/**
	 * add target to load balance target list.
	 *
	 * @param t the t
	 */
	void recoverTarget(String t);
	
	/**
	 * Gets the targets.
	 *
	 * @return the targets
	 */
	Set<String> getTargets();
	
	/**
	 * Checks for targets.
	 *
	 * @return true if availabled load balance target list is not empty
	 */
	boolean hasTargets();
	
	/**
	 * Gets the failed targets.
	 *
	 * @return the failed targets
	 */
	Set<String> getFailedTargets();
	
}
