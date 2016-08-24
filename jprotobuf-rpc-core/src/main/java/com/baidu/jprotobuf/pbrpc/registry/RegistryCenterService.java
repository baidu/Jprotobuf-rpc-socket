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
package com.baidu.jprotobuf.pbrpc.registry;

import java.util.List;

/**
 * RegistryService. (SPI, Prototype, ThreadSafe)
 * 
 * @author xiemalin
 * @since 2.27
 */
public interface RegistryCenterService {

    /**
     * 注册数据，比如：提供者地址，消费者地址，路由规则，覆盖规则，等数据。.
     *
     * @param url 注册信息，不允许为空
     */
    void register(RegisterInfo url);

    /**
     * 取消注册.
     * 
     * @param url 注册信息，不允许为空
     */
    void unregister(RegisterInfo url);

    /**
     * 订阅符合条件的已注册数据，当有注册数据变更时自动推送.
     * 
     * @param url 订阅条件，不允许为空，
     * @param listener 变更事件监听器，不允许为空
     */
    void subscribe(RegisterInfo url, NotifyListener listener);

    /**
     * 取消订阅.
     * 
     * @param url 订阅条件，不允许为空
     * @param listener 变更事件监听器，不允许为空
     */
    void unsubscribe(RegisterInfo url, NotifyListener listener);

    /**
     * 查询符合条件的已注册数据，与订阅的推模式相对应，这里为拉模式，只返回一次结果。.
     *
     * @param url 查询条件，不允许为空，
     * @return 已注册信息列表，可能为空，
     * @see com.alibaba.dubbo.registry.NotifyListener#notify(List)
     */
    List<RegisterInfo> lookup(RegisterInfo url);

}