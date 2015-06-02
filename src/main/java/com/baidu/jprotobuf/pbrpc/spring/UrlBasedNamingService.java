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
package com.baidu.jprotobuf.pbrpc.spring;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.NumberUtils;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;

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

    private List<InetSocketAddress> list;

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

        list = new ArrayList<InetSocketAddress>();
        String[] array = url.split(";");
        for (String uri : array) {
            String[] uris = uri.split(":");
            if (uris != null && uris.length == 2) {
                list.add(new InetSocketAddress(uris[0], NumberUtils.parseNumber(uris[1], Integer.class)));
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingService#list()
     */
    @Override
    public List<InetSocketAddress> list() throws Exception {
        return list;
    }

}
