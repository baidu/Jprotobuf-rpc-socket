/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * PbRPC definition annotation.
 * 
 * @author xiemalin
 * @since 1.0
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProtobufPRC {
    
    /**
     * <pre>
     * The service name for protobuf RPC definition.
     * </pre>
     * 
     * @return the name of unique service in one publish server
     */
    String serviceName();

    /**
     * <pre>
     * The method name for protobuf RPC definition.
     * </pre>
     * 
     * @return the name of method
     */
    String methodName() default "";

    /**
     * Log ID generator.
     * @return class instance of LogIDGenerator
     */
    Class<? extends LogIDGenerator> logIDGenerator() default DummyLogIDGenerator.class;
    
    /**
     * @return once talk timeout in milliseconds.
     */
    long onceTalkTimeout() default 0; 
    
    /**
     * attachment handler
     * @return class instance of ClientAttachmentHandler
     */
    Class<? extends ClientAttachmentHandler> attachmentHandler() default DummyClientAttachmentHandler.class;

    /**
     * Compress type. 
     * @see CompressType
     */
    CompressType compressType() default CompressType.NO;
}
