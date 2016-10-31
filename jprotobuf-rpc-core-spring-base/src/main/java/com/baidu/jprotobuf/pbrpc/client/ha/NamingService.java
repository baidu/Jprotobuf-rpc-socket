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
package com.baidu.jprotobuf.pbrpc.client.ha;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;

/**
 * Naming service interface.
 * 
 * @author xiemalin
 * @since 2.15
 */
public interface NamingService {

    /**
     * get server list from naming service.
     * 
     * @param serviceSignatures service signatures
     * @return server list mapped by service signature.
     * @throws Exception in case of any exception
     */
    Map<String, List<RegisterInfo>> list(Set<String> serviceSignatures) throws Exception;

}
