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
package com.baidu.jprotobuf.pbrpc.spring;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.NumberUtils;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;

/**
 * {@link URL} based naming service provider.<br>
 * <pre>
 * {@code
 *  <bean id="namingService" class="com.baidu.jprotobuf.pbrpc.spring.UrlBasedNamingService">
 *       <constructor-arg>
 *           <value>localhost:1031;localhost:1032;localhost:1033</value>
 *       </constructor-arg>
 *   </bean>
 * }
 * </pre>
 * 
 * @author xiemalin
 * @since 2.17
 */
public class UrlBasedNamingService implements NamingService {

    /** The list. */
    private List<RegisterInfo> list;

    /**
     * url pattern with host:port and split by comma.<br>
     * eg: localhost:1031;localhost:1032
     * 
     * @param url in string
     */
    public UrlBasedNamingService(String url) {
        if (url == null) {
            list = Collections.emptyList();
            return;
        }

        list = new ArrayList<RegisterInfo>();
        String[] array = url.split(";");
        for (String uri : array) {
            String[] uris = uri.split(":");
            if (uris != null && uris.length == 2) {
                RegisterInfo registerInfo = new RegisterInfo();
                registerInfo.setHost(uris[0]);
                registerInfo.setPort((Integer) NumberUtils.parseNumber(uris[1], Integer.class));
                list.add(registerInfo);
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingService#list()
     */
    @Override
    public Map<String, List<RegisterInfo>> list(Set<String> services) throws Exception {
        Map<String, List<RegisterInfo>> ret = new HashMap<String, List<RegisterInfo>>();
        
        if (services == null || services.isEmpty()) {
            return ret;
        }
        for (String string : services) {
            ret.put(string, list);
        }
        
        return ret;
    }

}
