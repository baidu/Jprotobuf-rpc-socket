/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.baidu.jprotobuf.pbrpc.spring.PlaceholderResolver;

/**
 * Annotation parser call back interface.
 * 
 * @see CommonAnnotationBeanPostProcessor
 * 
 * @author xiemalin
 * @since 2.17
 */
public interface AnnotationParserCallback {

    /**
     * process all annotation on class type.
     * 
     * @param t
     *            annotation instance.
     * @param bean
     *            target bean
     * @param beanName
     *            target bean name
     * @param beanFactory
     *            spring bean factory
     * @return wrapped bean
     * @throws BeansException
     *             exceptions on spring beans create error.
     */
    Object annotationAtType(Annotation t, Object bean, String beanName,
        ConfigurableListableBeanFactory beanFactory) throws BeansException;

    /**
     * process all annotation on class type after spring containter started
     * 
     * @param t
     *            annotation instance.
     * @param bean
     *            target bean
     * @param beanName
     *            target bean name
     * @param beanFactory
     *            spring bean factory
     * @throws BeansException
     *             exceptions on spring beans create error.
     */
    void annotationAtTypeAfterStarted(Annotation t, Object bean,
        String beanName, ConfigurableListableBeanFactory beanFactory)
        throws BeansException;

    /**
     * process all annotation on class field.
     * 
     * @param t
     *            annotation instance.
     * @param value
     *            field value of target target
     * @param beanName
     *            target bean name
     * @param pvs
     *            bean property values
     * @param beanFactory
     *            spring bean factory
     * @param field
     *            field instance
     * @return field value
     * @throws BeansException
     *             exceptions on spring beans create error.
     */
    Object annotationAtField(Annotation t, Object value, String beanName,
        PropertyValues pvs, ConfigurableListableBeanFactory beanFactory,
        Field field) throws BeansException;

    /**
     * process all annotation on class method.
     * 
     * @param t
     *            annotation instance.
     * @param bean
     *            target bean
     * @param beanName
     *            target bean name
     * @param pvs
     *            bean property values
     * @param beanFactory
     *            spring bean factory
     * @param method
     *            method instance
     * @return method invoke parameter
     * @throws BeansException
     *             exceptions on spring beans create error.
     */
    Object annotationAtMethod(Annotation t, Object bean, String beanName,
        PropertyValues pvs, ConfigurableListableBeanFactory beanFactory,
        Method method) throws BeansException;

    /**
     * get annotation type on class type
     * 
     * @return annotation type on class type
     */
    Class<? extends Annotation> getTypeAnnotation();

    /**
     * get annotation type on class field or method
     * 
     * @return annotation type on class field or method
     */
    List<Class<? extends Annotation>> getMethodFieldAnnotation();

    /**
     * do destroy action on spring container close.
     * 
     * @throws Exception
     *             throw any exception
     */
    void destroy() throws Exception;

    /**
     * set {@link PlaceholderResolver} instance.
     * 
     * @param resolver
     *            {@link PlaceholderResolver} instance.
     */
    void setPlaceholderResolver(PlaceholderResolver resolver);

}
