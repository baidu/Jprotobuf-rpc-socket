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

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;

/**
 * {@link NamingService} support load balance strategy.
 *
 * @author xiemalin
 * @since 2.17
 */
public interface NamingServiceLoadBalanceStrategy extends LoadBalanceStrategy {

    /**
     * do reinit once by the naming service.
     *
     * @param serviceSignature the service signature
     * @param namingService {@link NamingService}
     */
    void doReInit(String serviceSignature, NamingService namingService);
}
