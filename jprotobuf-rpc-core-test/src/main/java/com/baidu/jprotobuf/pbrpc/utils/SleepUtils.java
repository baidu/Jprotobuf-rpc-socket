/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility tool for test usage.
 *
 * @author xiemalin
 * @since 2.19
 */
public class SleepUtils {
    
    private static final Logger LOG = Logger.getLogger(SleepUtils.class.getName());

    /**
     * time sleep for current thread.
     * 
     * @param sleepTimeInMilliseconds
     */
    public static void dummySleep(int sleepTimeInMilliseconds) {
        try {
            Thread.sleep(sleepTimeInMilliseconds);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e.getCause());
        }
    }
}
