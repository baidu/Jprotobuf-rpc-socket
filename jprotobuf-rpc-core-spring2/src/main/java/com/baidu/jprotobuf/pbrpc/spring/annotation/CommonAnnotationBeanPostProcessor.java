/*
 * Copyright 2002-2007 the original author or authors.
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
package com.baidu.jprotobuf.pbrpc.spring.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.baidu.jprotobuf.pbrpc.spring.PlaceholderResolver;
import com.baidu.jprotobuf.pbrpc.spring.PropertyPlaceholderConfigurerTool;

/**
 * Common annotation bean post processor. it uses {@link AnnotationParserCallback}<br>
 * interface to define specified {@link Annotation} then recognize the Class to do <br>
 * bean define action
 * 
 * @see AnnotationParserCallback
 * @author xiemalin
 * @since 1.0.0.0
 */
public class CommonAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware, DisposableBean,
        InitializingBean, ApplicationListener {
    private static final Log LOGGER = LogFactory.getLog(AutowiredAnnotationBeanPostProcessor.class);

    private AnnotationParserCallback callback;

    private int order = Ordered.LOWEST_PRECEDENCE - 3;

    private ConfigurableListableBeanFactory beanFactory;

    private final Map<Class<?>, InjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap<Class<?>, InjectionMetadata>();

    private Properties propertyResource;

    private PlaceholderResolver resolver;

    private Vector<BeanInfo> typeAnnotationedBeans = new Vector<BeanInfo>();

    /**
     * status to control start only once
     */
    private AtomicBoolean started = new AtomicBoolean(false);

    /**
     * @return the callback
     */
    private AnnotationParserCallback getCallback() {
        return callback;
    }

    /**
     * @param callback the callback to set
     */
    public void setCallback(AnnotationParserCallback callback) {
        this.callback = callback;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "CommonAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (getCallback() == null) {
            return super.postProcessBeforeInitialization(bean, beanName);
        }

        Class clazz = bean.getClass();
        Class<? extends Annotation> annotation;
        annotation = getCallback().getTypeAnnotation();
        if (annotation == null) {
            return bean;
        }
        Annotation a = clazz.getAnnotation(annotation);
        if (a == null) {
            return bean;
        }
        BeanInfo beanInfo = new BeanInfo(bean, beanName, a);
        typeAnnotationedBeans.add(beanInfo);
        return getCallback().annotationAtType(a, bean, beanName, beanFactory);
    }

    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class beanType, String beanName) {
        if (beanType != null && getCallback() != null && getCallback().getMethodFieldAnnotation() != null) {

            List<Class<? extends Annotation>> methodFieldAnnotation = getCallback().getMethodFieldAnnotation();
            if (methodFieldAnnotation != null) {
                InjectionMetadata metadata = findAnnotationMetadata(beanType, methodFieldAnnotation);
                metadata.checkConfigMembers(beanDefinition);
            }

        }
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        List<Class<? extends Annotation>> methodFieldAnnotation = getCallback().getMethodFieldAnnotation();
        if (getCallback() == null || methodFieldAnnotation == null) {
            return true;
        }

        InjectionMetadata metadata = findAnnotationMetadata(bean.getClass(), methodFieldAnnotation);
        try {
            metadata.injectFields(bean, beanName);
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Autowiring service of fields failed", ex);
        }

        return true;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
            String beanName) throws BeansException {
        List<Class<? extends Annotation>> methodFieldAnnotation = getCallback().getMethodFieldAnnotation();
        if (getCallback() == null || methodFieldAnnotation == null) {
            return pvs;
        }

        InjectionMetadata metadata = findAnnotationMetadata(bean.getClass(), methodFieldAnnotation);
        try {
            metadata.injectMethods(bean, beanName, pvs);
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Autowiring of methods failed", ex);
        }

        return pvs;
    }

    private InjectionMetadata findAnnotationMetadata(final Class clazz,
            final List<Class<? extends Annotation>> annotion) {
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(clazz);
        if (metadata == null) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(clazz);
                if (metadata == null) {
                    final InjectionMetadata newMetadata = new InjectionMetadata(clazz);
                    ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                        public void doWith(Field field) {
                            for (Class<? extends Annotation> anno : annotion) {
                                Annotation annotation = field.getAnnotation(anno);
                                if (annotation != null) {
                                    if (Modifier.isStatic(field.getModifiers())) {
                                        throw new IllegalStateException(
                                                "Autowired annotation is not supported on static fields");
                                    }
                                    newMetadata.addInjectedField(new AutowiredFieldElement(field, annotation));
                                }

                            }
                        }
                    });
                    ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
                        public void doWith(Method method) {
                            for (Class<? extends Annotation> anno : annotion) {
                                Annotation annotation = method.getAnnotation(anno);
                                if (annotation != null
                                        && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                                    if (Modifier.isStatic(method.getModifiers())) {
                                        throw new IllegalStateException(
                                                "Autowired annotation is not supported on static methods");
                                    }
                                    if (method.getParameterTypes().length == 0) {
                                        throw new IllegalStateException(
                                                "Autowired annotation requires at least one argument: " + method);
                                    }
                                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
                                    newMetadata.addInjectedMethod(new AutowiredMethodElement(method, annotation, pd));
                                }

                            }
                        }
                    });
                    metadata = newMetadata;
                    this.injectionMetadataCache.put(clazz, metadata);
                }
            }
        }
        return metadata;
    }

    /**
     * Class representing injection information about an annotated field.
     */
    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

        private final Annotation annotation;

        public AutowiredFieldElement(Field field, Annotation annotation) {
            super(field, null);
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            try {
                ReflectionUtils.makeAccessible(field);
                Object value = field.get(bean);

                value = getCallback().annotationAtField(annotation, value, beanName, pvs, beanFactory, field);

                if (value != null) {
                    ReflectionUtils.makeAccessible(field);
                    field.set(bean, value);
                }
            } catch (Throwable ex) {
                throw new BeanCreationException("Could not autowire field: " + field, ex);
            }
        }
    }

    /**
     * Class representing injection information about an annotated method.
     */
    private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

        private final Annotation annotation;

        public AutowiredMethodElement(Method method, Annotation annotation, PropertyDescriptor pd) {
            super(method, pd);
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            if (this.skip == null && this.pd != null && pvs != null && pvs.contains(this.pd.getName())) {
                // Explicit value provided as part of the bean definition.
                this.skip = Boolean.TRUE;
            }
            if (this.skip != null && this.skip.booleanValue()) {
                return;
            }
            Method method = (Method) this.member;
            try {
                Object[] arguments = null;

                Class[] paramTypes = method.getParameterTypes();
                arguments = new Object[paramTypes.length];

                for (int i = 0; i < arguments.length; i++) {
                    MethodParameter methodParam = new MethodParameter(method, i);
                    GenericTypeResolver.resolveParameterType(methodParam, bean.getClass());
                    arguments[i] =
                            getCallback().annotationAtMethod(annotation, bean, beanName, pvs, beanFactory, method);

                    if (arguments[i] == null) {
                        arguments = null;
                        break;
                    }
                }

                if (this.skip == null) {
                    if (this.pd != null && pvs instanceof MutablePropertyValues) {
                        ((MutablePropertyValues) pvs).registerProcessedProperty(this.pd.getName());
                    }
                    this.skip = Boolean.FALSE;
                }
                if (arguments != null) {
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(bean, arguments);
                }
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } catch (Throwable ex) {
                throw new BeanCreationException("Could not autowire method: " + method, ex);
            }
        }
    }

    public void destroy() throws Exception {
        if (getCallback() != null) {
            getCallback().destroy();
        }

    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getCallback(), "property 'callbck' must be set");

        propertyResource = PropertyPlaceholderConfigurerTool
                .getRegisteredPropertyResourceConfigurer((ConfigurableListableBeanFactory) beanFactory);

        if (propertyResource != null) {
            propertyResource.putAll(System.getProperties());
        }
        if (resolver == null) {
            resolver = PropertyPlaceholderConfigurerTool.createPlaceholderParser(propertyResource);
        }

        if (getCallback() != null) {
            getCallback().setPlaceholderResolver(resolver);
        }

    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
            // only execute this method once. bug fix for ContextRefreshedEvent will invoke twice on spring MVC servlet
            if (started.compareAndSet(false, true)) {
                for (BeanInfo bean : typeAnnotationedBeans) {
                    if (getCallback() != null) {
                        Object targetBean = beanFactory.getBean(bean.name);
                        getCallback().annotationAtTypeAfterStarted(bean.annotation, targetBean, bean.name, beanFactory);
                    }
                }
            } else {
                LOGGER.warn("onApplicationEvent of application event [" + event
                        + "] ignored due to processor already started.");
            }
        }

    }

    private static class BeanInfo {
        Object bean;
        String name;
        Annotation annotation;

        /**
         * @param bean
         * @param name
         */
        public BeanInfo(Object bean, String name, Annotation annotation) {
            super();
            this.bean = bean;
            this.name = name;
            this.annotation = annotation;
        }

    }

}
