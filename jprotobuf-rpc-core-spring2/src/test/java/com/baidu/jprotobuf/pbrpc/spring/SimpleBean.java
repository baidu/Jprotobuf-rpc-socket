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
package com.baidu.jprotobuf.pbrpc.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 *
 * @author xiemalin
 *
 */
@Service("simpleBean")
public class SimpleBean implements InitializingBean, ApplicationListener, BeanFactory  {

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String)
     */
    @Override
    public Object getBean(String name) throws BeansException {
        return null;
    }


    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String, java.lang.Object[])
     */
    @Override
    public Object getBean(String name, Object...args) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#containsBean(java.lang.String)
     */
    @Override
    public boolean containsBean(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#isSingleton(java.lang.String)
     */
    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#isPrototype(java.lang.String)
     */
    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#getType(java.lang.String)
     */
    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactory#getAliases(java.lang.String)
     */
    @Override
    public String[] getAliases(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getBean(String name, Class requiredType) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

}
