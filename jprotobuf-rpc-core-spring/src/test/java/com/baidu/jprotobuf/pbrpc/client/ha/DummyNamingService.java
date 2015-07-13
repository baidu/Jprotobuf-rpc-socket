/*
 * Copyright 2002-2014 the original author or authors.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;

/**
 * A dummy {@link NamingService} implements support default server list.
 *
 * @author xiemalin
 * @since 2.15
 */
public class DummyNamingService implements NamingService {
    
    private List<RegisterInfo> list;
    
    /**
     * @param list
     */
    public DummyNamingService(List<RegisterInfo> list) {
        super();
        this.list = list;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingService#list()
     */
    public List<RegisterInfo> list() throws Exception {
        return list;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingService#list(java.util.Set)
     */
    @Override
    public Map<String, List<RegisterInfo>> list(Set<String> serviceSignatures) throws Exception {
        Map<String, List<RegisterInfo>> ret = new HashMap<String, List<RegisterInfo>>();
        
        if (serviceSignatures == null || serviceSignatures.isEmpty()) {
            return ret;
        }
        for (String string : serviceSignatures) {
            ret.put(string, list);
        }
        
        return ret;
    }

}
