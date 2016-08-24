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
 * NotifyListener. (API, Prototype, ThreadSafe)
 * 
 * @author xiemalin
 * @since 2.27
 */
public interface NotifyListener {

    /**
     * 当收到服务变更通知时触发。.
     *
     * @param urls 已注册信息列表，总不为空，
     */
    void notify(List<RegisterInfo> urls);

}