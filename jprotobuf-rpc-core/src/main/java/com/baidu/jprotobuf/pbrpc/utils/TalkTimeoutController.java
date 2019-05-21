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
package com.baidu.jprotobuf.pbrpc.utils;

/**
 * to change once talk time out setting dynamically.
 *
 * @author xiemalin
 * @since 3.5.22
 */
public class TalkTimeoutController {

    /** The Constant TALK_TIMEOUT. */
    private static final ThreadLocal<Long> TALK_TIMEOUT = new ThreadLocal<Long>();
    
    /** The enable once. */
    private static boolean enableOnce = true;
    
    /**
     * Sets the enable once.
     *
     * @param enableOnce the new enable once
     */
    public static void setEnableOnce(boolean enableOnce) {
        TalkTimeoutController.enableOnce = enableOnce;
    }
    
    /**
     * Checks if is enable once.
     *
     * @return true, if is enable once
     */
    public static boolean isEnableOnce() {
        return enableOnce;
    }
    
    /**
     * Sets the talk timeout.
     *
     * @param talkTimeout the new talk timeout
     */
    public static void setTalkTimeout(long talkTimeout) {
        if (talkTimeout > 0) {
            TALK_TIMEOUT.set(talkTimeout);
        } else {
            clearTalkTimeout();
        }
    }
    
    /**
     * Gets the talk timeout.
     *
     * @return the talk timeout
     */
    public static long getTalkTimeout() {
        Long talkTimeout = TALK_TIMEOUT.get();
        if (talkTimeout != null) {
            return talkTimeout;
        }
        
        return 0;
    }
    
    /**
     * Clear talk timeout.
     */
    public static void clearTalkTimeout() {
        TALK_TIMEOUT.remove();
    }
    
}
