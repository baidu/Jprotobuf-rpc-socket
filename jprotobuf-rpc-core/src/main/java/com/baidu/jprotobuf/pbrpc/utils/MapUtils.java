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
package com.baidu.jprotobuf.pbrpc.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility tool for {@link Map} class.
 *
 * @author xiemalin
 * @since 2.27
 */
public class MapUtils {
    
    /** The Constant KV_SEPERATE. */
    private static final char KV_SEPERATE = '=';

    /**
     * Null-safe check if the specified map is empty.
     * <p>
     * Null returns true.
     * 
     * @param map  the map to check, may be null
     * @return true if empty or null
     * @since Commons Collections 3.2
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Join.
     *
     * @param map the map
     * @return the string
     */
    public static String join(Map<String, String> map) {
        return join(map, ',');
    }
    
    /**
     * Join.
     *
     * @param map the map
     * @param separator  the separator character to use
     * @return the string
     */
    public static String join(Map<String, String> map, char separator) {
        if (isEmpty(map)) {
            return StringUtils.EMPTY_STRING;
        }
        
        StringBuilder ret = new StringBuilder();
        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            
            ret.append(StringUtils.escapeJava(entry.getKey()));
            ret.append("=");
            ret.append(StringUtils.escapeJava(entry.getValue()));
            
            if (iter.hasNext()) {
                ret.append(",");
            }
        }
        return ret.toString();
        
    }
    
    /**
     * Parses the map.
     *
     * @param joinString the join string
     * @return the map
     */
    public static Map<String, String> parseMap(String joinString) {
        return parseMap(joinString, ',');
    }
    
    /**
     * Parses the map.
     *
     * @param joinString the join string
     * @param separator the separator
     * @return the map
     */
    public static Map<String, String> parseMap(String joinString, char separator) {
        if (StringUtils.isBlank(joinString)) {
            return Collections.emptyMap();
        }
        
        if (joinString.indexOf(KV_SEPERATE) == -1) {
            throw new RuntimeException("Invalid join string to parse to Map. string=" + joinString);
        }
        
        Map<String, String> ret = new HashMap<String, String>();
        String[] split = StringUtils.split(joinString, separator);
        for (String kv : split) {
            if (kv.indexOf(KV_SEPERATE) == -1) {
                throw new RuntimeException("Invalid join string to parse to Map. string=" + joinString);
            }
            
            String[] kvArray = StringUtils.split(kv, KV_SEPERATE + "", 2);
            if (kvArray == null || kvArray.length != 2) {
                throw new RuntimeException("Invalid join string to parse to Map. string=" + joinString);
            }
            ret.put(StringUtils.unescapeJava(kvArray[0]), StringUtils.unescapeJava(kvArray[1]));
        }
        
        return ret;
    }
    
}
