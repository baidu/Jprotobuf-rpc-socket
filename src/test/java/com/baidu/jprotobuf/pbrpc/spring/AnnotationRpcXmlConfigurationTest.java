/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.spring;

import org.junit.Test;

/**
 * 
 * 
 * @author xiemalin
 * @since 2.17
 */
public class AnnotationRpcXmlConfigurationTest extends RpcXmlConfigurationTestBase {

    protected String getConfigurationPath() {
        return "classpath:" + AnnotationRpcXmlConfigurationTest.class.getName().replace('.', '/') + ".xml";
    }

    @Test
    public void testCommonRpcRequest() {

        AnnotationEchoServiceClient annotationEchoServiceClient =
                context.getBean("echoServiceClient", AnnotationEchoServiceClient.class);
        
        // test common client
        super.internalRpcRequestAndResponse(annotationEchoServiceClient.echoService);

    }
    
    @Test
    public void testHaRpcRequest() {

        AnnotationEchoServiceClient annotationEchoServiceClient =
                context.getBean("echoServiceClient", AnnotationEchoServiceClient.class);
        
        // test ha client
        super.internalRpcRequestAndResponse(annotationEchoServiceClient.haEchoService);
        
    }
    
    @Test
    public void testHaRpcRequestWithPartialFailed() {

        AnnotationEchoServiceClient annotationEchoServiceClient =
                context.getBean("echoServiceClient", AnnotationEchoServiceClient.class);
        
        // test ha client
        super.internalRpcRequestAndResponse(annotationEchoServiceClient.haEchoServiceOfPartialFailed);
    }
}
