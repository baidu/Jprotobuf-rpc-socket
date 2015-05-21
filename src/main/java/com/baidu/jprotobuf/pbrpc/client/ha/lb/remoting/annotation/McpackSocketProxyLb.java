package com.baidu.bjf.lb.remoting.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.baidu.bjf.remoting.mcpacksocket.annotation.McpackSocketProxy;

/**
 * Load balance annotation for Mcpack Socket Proxy
 * 
 * @see McpackSocketProxyLbAnnotationParser
 * @author lijianbin
 * @since 1.0.4.0
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Component
public @interface McpackSocketProxyLb {
    /**
     * array of McpackSocketProxy
     */
    McpackSocketProxy[] value();
    
    /**
     * set from spring ioc container. if this set property 'loadBalanceStrategy'
     * will disabled
     */
    String loadBalanceStrategyBeanName() default "";
    
    /**
     * set from spring ioc container. if this set property 'failOverInterceptor'
     * will disabled
     */
    String failOverInterceptorBeanName() default "";
    
    /**
     * reover interval,
     */
    long recoverInterval() default 1000L;
    
    /**
     * fail over event name
     */
    String failOverEventBeanName() default "";
}
