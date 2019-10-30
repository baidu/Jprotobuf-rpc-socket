/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.pbrpc.spring.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baidu.jprotobuf.pbrpc.spring.annotation.CommonAnnotationBeanPostProcessor;
import com.baidu.jprotobuf.pbrpc.spring.annotation.ProtobufRpcAnnotationResolver;

/**
 * spring boot configuration class
 * 
 * @author xiemalin
 * @since 3.5.25
 */
@Configuration
public class PbRPCConfiguration {
    
    
    /**
     * Gets the common annotation bean post processor.
     *
     * @return the common annotation bean post processor
     */
    @Bean
    public CommonAnnotationBeanPostProcessor getCommonAnnotationBeanPostProcessor() {
        CommonAnnotationBeanPostProcessor cabp = new CommonAnnotationBeanPostProcessor();
        
        ProtobufRpcAnnotationResolver resolver = new ProtobufRpcAnnotationResolver();
        cabp.setCallback(resolver);
        
        return cabp;
    }

}
