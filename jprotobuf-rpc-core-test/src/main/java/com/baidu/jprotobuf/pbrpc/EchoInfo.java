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

package com.baidu.jprotobuf.pbrpc;

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
    
    @Protobuf(description = "Echo消息内容")
    private String message;
    
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EchoInfo [message=");
        builder.append(message);
        builder.append("]");
        return builder.toString();
    }
    
    

}
