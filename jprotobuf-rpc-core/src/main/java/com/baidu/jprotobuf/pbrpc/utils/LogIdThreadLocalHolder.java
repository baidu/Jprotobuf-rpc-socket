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

/**
 * Hold log id message info under current thread local .
 *
 * @author xiemalin
 * @since 3.2.8
 */
public class LogIdThreadLocalHolder {

    /** log id holder. */
    private static final ThreadLocal<Long> LOG_ID_HOLDER = new ThreadLocal<Long>();
    
    /**
     * Sets the log id.
     *
     * @param logId the new log id
     */
    public static void setLogId(Long logId) {
        if (logId != null) {
            LOG_ID_HOLDER.set(logId);
        }
    }
    
    /**
     * Gets the log id.
     *
     * @return the log id
     */
    public static Long getLogId() {
        return LOG_ID_HOLDER.get();
    }
    
    /**
     * clear current log id under current thread scope.
     */
    public static void clearLogId() {
        LOG_ID_HOLDER.remove();
    }
}
