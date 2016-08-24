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

import org.aopalliance.intercept.MethodInvocation;

import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverEvent;

/**
 * load balance strategy intercepter. <br>
 * it implements from {@link FailOverEvent}
 * 
 * @author xiemalin
 * @see FailOverEvent
 * @since 2.16
 */
public interface StrategyInterceptor extends FailOverEvent {

    /**
     * before strategy elect method invoke.
     *
     * @param lbStratety {@link LoadBalanceStrategy}
     * @param invocation {@link MethodInvocation}
     */
    void beforeElection(LoadBalanceStrategy lbStratety,
            MethodInvocation invocation);

    /**
     * after strategy elect method invoke.
     *
     * @param electKey elect returned target name
     * @param invocation {@link MethodInvocation}
     */
    void afterElection(String electKey, MethodInvocation invocation);

    /**
     * return if need to do elect method.
     * 
     * @param invocation {@link MethodInvocation}
     * @return true if need to do elect method
     */
    boolean isDoElection(MethodInvocation invocation);

    /**
     * do elect action.
     *
     * @param invocation {@link MethodInvocation}
     * @return the string
     */
    String elect(MethodInvocation invocation);

}
