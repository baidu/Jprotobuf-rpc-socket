/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.spring;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.ReflectionUtils;

/**
 * Utility class for {@link PropertyPlaceholderConfigurer}
 * 
 * @author xiemalin
 * @since 2.17
 */
public final class PropertyPlaceholderConfigurerTool {

    /**
     * get {@link Properties} instance from  {@link ConfigurableListableBeanFactory}
     * 
     * @param beanFactory spring container
     * @return {@link Properties} instance
     */
    public static Properties getRegisteredPropertyResourceConfigurer(
            ConfigurableListableBeanFactory beanFactory) {
        Class clazz = PropertyPlaceholderConfigurer.class;
        Map beans = beanFactory.getBeansOfType(clazz);
        if (beans == null || beans.isEmpty()) {
            return null;
        }
        
        Object config = ((Map.Entry)beans.entrySet().iterator().next()).getValue();
        if (clazz.isAssignableFrom(config.getClass())) {
            Method m = ReflectionUtils.findMethod(clazz, "mergeProperties");
            if (m != null) {
                m.setAccessible(true);
                return (Properties) ReflectionUtils.invokeMethod(m, config);
            }
        }
        return null;
    }
    
    /**
     * To create placeholder parser
     * @param propertyResource {@link Properties} instance
     * @return {@link PlaceholderResolver} instance
     */
    public static PlaceholderResolver createPlaceholderParser(
            final Properties propertyResource) {
        if (propertyResource == null) {
            return null;
        }
        PlaceholderResolver resolver = new PlaceholderResolver(
                new PlaceholderResolved() {
                    public String doResolved(String placeholder) {
                        return propertyResource.getProperty(placeholder);
                    }
                });
        return resolver;
    }
}
