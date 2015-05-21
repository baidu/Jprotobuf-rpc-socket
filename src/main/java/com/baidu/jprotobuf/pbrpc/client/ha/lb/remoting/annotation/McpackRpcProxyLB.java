/**
 * 
 */
package com.baidu.bjf.lb.remoting.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.baidu.bjf.remoting.mcpack.annotation.McpackRpcProxy;

/**
 * @author xiemalin
 * @since 1.0.0.0
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface McpackRpcProxyLB {

    McpackRpcProxy[] value();
    
    /**
     * @return set from spring ioc container. if this set property
     *         'loadBalanceStrategy' will disabled
     */
    String loadBalanceStrategyBeanName() default "";

    /**
     * @return set from spring ioc container. if this set property
     *         'failOverInterceptor' will disabled
     */
    String failOverInterceptorBeanName() default "";

    /**
     * @return
     */
    long recoverInterval() default 1000L;

    /**
     * @return
     */
    String failOverEventBeanName() default "";    
}
