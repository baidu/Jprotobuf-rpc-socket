/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import com.baidu.jprotobuf.pbrpc.ProtobufPRC;

/**
 * Simple echo service interface
 *
 * @author xiemalin
 * @since 1.0
 */
public interface EchoService {

    /**
     * To define a RPC client method. <br>
     * serviceName is "echoService"
     * methodName is use default method name "echo"
     * onceTalkTimeout is 200 milliseconds
     * 
     * @param info
     * @return
     */
    @ProtobufPRC(serviceName = "echoService", onceTalkTimeout = 200)
    EchoInfo echo(EchoInfo info);
}
