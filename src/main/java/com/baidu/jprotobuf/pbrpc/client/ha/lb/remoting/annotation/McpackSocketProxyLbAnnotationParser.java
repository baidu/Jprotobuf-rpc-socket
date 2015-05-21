package com.baidu.bjf.lb.remoting.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.baidu.bjf.beans.context.annotation.AbstractAnnotationParserCallback;
import com.baidu.bjf.lb.LoadBalanceProxyFactoryBean;
import com.baidu.bjf.lb.failover.FailOverEvent;
import com.baidu.bjf.lb.failover.FailOverInterceptor;
import com.baidu.bjf.lb.strategy.LoadBalanceStrategy;
import com.baidu.bjf.lb.strategy.RoundRobinLoadBalanceStrategy;
import com.baidu.bjf.lb.remoting.failover.mcpacksocket.McpackSocketFailOverInterceptor;
import com.baidu.bjf.remoting.mcpacksocket.McpackSocketProxyFactoryBean;
import com.baidu.bjf.remoting.mcpacksocket.McpackSocketServiceCreator;
import com.baidu.bjf.remoting.mcpacksocket.annotation.McpackSocketProxy;
import com.baidu.bjf.remoting.mcpacksocket.io.support.Client;

/**
 * Annotation parser for McpackSocketProxyLb.
 * 
 * @see AbstractAnnotationParserCallback
 * @author lijianbin
 * @since 1.0.4.0
 */
public class McpackSocketProxyLbAnnotationParser extends AbstractAnnotationParserCallback {
    
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(McpackSocketProxyLbAnnotationParser.class);
    
    /**
     * default load balance factor
     */
    private static final int DEFAULT_LB_FACTOR = 1;
    
    /**
     * vector of load balance proxy beans
     */
    private static Vector<LoadBalanceProxyFactoryBean> lbProxyBeans = new Vector<LoadBalanceProxyFactoryBean>();
    
    /**
     * map contains mcpack clients, key: proxy's service url(ip:port), value:
     * mcpack client.
     */
    private static Map<String, Client> clientsMap = new ConcurrentHashMap<String, Client>();
    
    /**
     * vector of proxy beans
     */
    private static Vector<McpackSocketProxyFactoryBean> proxyBeans = new Vector<McpackSocketProxyFactoryBean>();
    
    /**
     * process all annotation on class type.
     * 
     * @param annotation
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
    public Object annotationAtType(Annotation annotation, Object bean, String beanName,
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        return bean;
    }
    
    /**
     * process all annotation on class type after spring containter started
     * 
     * @param annotation
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
    public void annotationAtTypeAfterStarted(Annotation annotation, Object bean, String beanName,
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // nothing to do
    }
    
    /**
     * process all annotation on class field.
     * 
     * @param annotation
     *            annotation instance.
     * @param bean
     *            target bean
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
    public Object annotationAtField(Annotation annotation, Object bean, String beanName,
            PropertyValues pvs, ConfigurableListableBeanFactory beanFactory, Field field)
            throws BeansException {
        
        if (annotation instanceof McpackSocketProxyLb) {
            LoadBalanceProxyFactoryBean factoryBean = createLbFactoryBean(
                    (McpackSocketProxyLb) annotation, beanFactory, this);
            lbProxyBeans.add(factoryBean);
            
            LOGGER.info("Annotation 'McpackSocketProxyLB' on field for target '" + beanName
                    + "' created");
            return factoryBean.getObject();
        }
        return bean;
        
    }
    
    /**
     * process all annotation on class method.
     * 
     * @param annotation
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
    public Object annotationAtMethod(Annotation annotation, Object bean, String beanName,
            PropertyValues pvs, ConfigurableListableBeanFactory beanFactory, Method method)
            throws BeansException {
        return bean;
    }
    
    /**
     * get annotation type on class type
     * 
     * @return annotation type on class type
     */
    public Class<? extends Annotation> getTypeAnnotation() {
        // type annotation is not supported
        return null;
    }
    
    /**
     * get annotation type on class field or method
     * 
     * @return annotation type on class field or method
     */
    public Class<? extends Annotation> getMethodFieldAnnotation() {
        return McpackSocketProxyLb.class;
    }
    
    /**
     * do destroy action on spring container close.
     * 
     * @throws Exception
     *             throw any excpetion
     */
    public void destroy() throws Exception {
        for (LoadBalanceProxyFactoryBean export : lbProxyBeans) {
            try {
                export.destroy();
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        lbProxyBeans.clear();
        for (McpackSocketProxyFactoryBean bean : proxyBeans) {
            try {
                bean.destroy();
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        proxyBeans.clear();
        
        for (Client client : clientsMap.values()) {
            client.stop();
        }
        
        clientsMap.clear();
        
    }
    
    /**
     * Create the load balanced proxy bean object
     * 
     * @param proxyLbInfo
     *            annotation for Mcpack Socket proxy load balancer
     *            (McpackSocketProxyLb)
     * @param beanFactory
     *            spring bean factory
     * @param callback
     *            annotation parser callback
     * @return load balanced proxy bean, instance of LoadBalanceProxyFactoryBean
     * @see LoadBalanceProxyFactoryBean
     */
    private LoadBalanceProxyFactoryBean createLbFactoryBean(McpackSocketProxyLb proxyLbInfo,
            ConfigurableListableBeanFactory beanFactory, AbstractAnnotationParserCallback callback) {
        // create load balance instance
        LoadBalanceProxyFactoryBean lbFactoryBean = new LoadBalanceProxyFactoryBean();
        
        // parse target Proxy
        McpackSocketProxy[] proxyInfos = proxyLbInfo.value();
        if (ArrayUtils.isEmpty(proxyInfos)) {
            throw new RuntimeException(
                    "Create annotation McpackSocketProxyLb failed due to no target specified");
        }
        
        Map<String, Integer> lbFactors = new HashMap<String, Integer>(proxyInfos.length);
        Map<String, Object> targets = new HashMap<String, Object>(proxyInfos.length);
        Map<String, Client> recoverClients = new HashMap<String, Client>(proxyInfos.length);
        
        Class<?> proxyClass = null;
        
        for (McpackSocketProxy proxyInfo : proxyInfos) {
            if (proxyClass == null) {
                proxyClass = proxyInfo.serviceInterface();
            }
            
            if (StringUtils.isBlank(proxyInfo.serviceUrl())) {
                throw new RuntimeException(
                        "@McpackSocketProxy create failed property 'serviceUrl' is null");
            }
            
            String serviceUrl = parsePlaceholder(proxyInfo.serviceUrl());
            
            Client client = clientsMap.get(serviceUrl);
            
            if (client == null) {
                client = McpackSocketServiceCreator.createMcpackClient(proxyInfo, callback);
                clientsMap.put(serviceUrl, client);
                client.start();
            }
            
            McpackSocketProxyFactoryBean proxyBean = McpackSocketServiceCreator
                    .createMcpackSocketProxy(proxyInfo, serviceUrl, client, this);
            
            if (proxyBean != null) {                
                proxyBeans.add(proxyBean);
                
                String name = createUniqueProxyName(proxyInfo);
                // use default load balance strategy factor
                lbFactors.put(name, DEFAULT_LB_FACTOR);
                targets.put(name, proxyBean.getObject());
                
                recoverClients.put(name, client);
            }
        }
        // set target beans
        lbFactoryBean.setTargetBeans(targets);
        // set service interface
        lbFactoryBean.setServiceInterface(proxyClass);
        
        // failOverInterceptorBeanName
        String failOverName = proxyLbInfo.failOverInterceptorBeanName();
        failOverName = callback.parsePlaceholder(failOverName);
        FailOverInterceptor failOverInterceptor = null;
        if (StringUtils.isBlank(failOverName)) {
            // using default
            failOverInterceptor = new McpackSocketFailOverInterceptor();
            ((McpackSocketFailOverInterceptor) failOverInterceptor)
                    .setMcpackClients(recoverClients);
        } else {
            // get from factory
            failOverInterceptor = (FailOverInterceptor) beanFactory.getBean(failOverName);
        }
        
        lbFactoryBean.setFailOverInterceptor(failOverInterceptor);
        
        // failover event
        String evenName = proxyLbInfo.failOverEventBeanName();
        if (StringUtils.isNotBlank(evenName)) {
            Object event = beanFactory.getBean(evenName);
            if (event == null) {
                LOGGER.error("event bean for " + evenName + " not found!");
            } else {
                if (event instanceof FailOverEvent) {
                    lbFactoryBean.setFailOverEvent((FailOverEvent) event);
                } else {
                    LOGGER.error("event for " + evenName + " is not instance of FailOverEvent");
                }
            }
        }
        
        // get load balance strategy
        String strategyName = proxyLbInfo.loadBalanceStrategyBeanName();
        strategyName = callback.parsePlaceholder(strategyName);
        LoadBalanceStrategy strategy = null;
        if (StringUtils.isNotBlank(strategyName)) {
            Object lbs = beanFactory.getBean(strategyName);
            if (lbs == null) {
                LOGGER.error("strategy bean for " + strategyName + " not found!");
            } else {
                if (lbs instanceof LoadBalanceStrategy) {
                    strategy = (LoadBalanceStrategy) lbs;
                } else {
                    LOGGER.error("strategy for " + strategyName
                            + " is not instance of LoadBalanceStrategy");
                }
            }
        }
        
        // load balance strategy
        if (strategy == null) {
            // using default
            strategy = new RoundRobinLoadBalanceStrategy(lbFactors);
        }
        lbFactoryBean.setLoadBalanceStrategy(strategy);
        
        try {
            lbFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return lbFactoryBean;
    }
    
    /**
     * create a unique name for a mcpack socket proxy. Result = uuid + ":" +
     * servieUrl
     * 
     * @param proxyInfo
     * @return unique proxy name
     */
    private String createUniqueProxyName(McpackSocketProxy proxyInfo) {
        return UUID.randomUUID().toString() + ":" + proxyInfo.serviceUrl();
    }
}
