/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport.handler;

/**
 * Error code list
 *
 * @author xiemalin
 * @since 1.0
 */
public class ErrorCodes {

    /**
     * success status
     */
    public static final int ST_SUCCESS = 0;
    
    /**
     * 未知异常
     */
    public static final int ST_ERROR = 1;
    
    /**
     * 方法未找到异常
     */
    public static final int ST_SERVICE_NOTFOUND = 2;
    
    /**
     * 压缩与解压异常
     */
    public static final int ST_ERROR_COMPRESS = 3;
    
    /**
     * 
     */
    public static final String MSG_SERVICE_NOTFOUND = "service not found";
    
    /**
     * read time out
     */
    public static final int ST_READ_TIMEOUT = 100;
    
    
    
    public static final String MSG_READ_TIMEOUT = "read time out";
    
    
    /**
     * check is error code is equals to ST_SUCCESS
     * @param errorCode 
     * @return
     */
    public static boolean isSuccess(int errorCode) {
        return ST_SUCCESS == errorCode;
    }
}
