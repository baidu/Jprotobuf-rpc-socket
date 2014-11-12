/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * Echo info class by using Jprotobuf annotation.
 * more information visit: https://github.com/jhunters/jprotobuf
 *
 * @author xiemalin
 * @since 1.0
 * @see EchoService
 */
public class EchoInfo {
    
    @Protobuf
    public String message;
    
    /**
     * 
     */
    public EchoInfo() {
    }
    
    /**
     * @param message
     */
    public EchoInfo(String message) {
        this.message = message;
    }

    /**
     * get the message
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * set message value to message
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
