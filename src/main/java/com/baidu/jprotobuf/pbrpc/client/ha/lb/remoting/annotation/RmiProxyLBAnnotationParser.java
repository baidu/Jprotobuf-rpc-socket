/**
 * 
 */
package com.baidu.bjf.lb.remoting.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.baidu.bjf.beans.context.annotation.AbstractAnnotationParserCallback;
import com.baidu.bjf.lb.LoadBalanceProxyFactoryBean;
import com.baidu.bjf.lb.failover.FailOverEvent;
import com.baidu.bjf.lb.failover.FailOverInterceptor;
import com.baidu.bjf.lb.remoting.LBAnnotationWrapper;
import com.baidu.bjf.lb.remoting.failover.rmi.RmiFailOverInterceptor;
import com.baidu.bjf.lb.remoting.strategy.rmi.RmiTransactionStrategyInterceptor;
import com.baidu.bjf.lb.strategy.LoadBalanceStrategy;
import com.baidu.bjf.lb.strategy.RoundRobinLoadBalanceStrategy;
import com.baidu.bjf.lb.strategy.StrategyInterceptor;
import com.baidu.bjf.management.annotation.ConnectionManaged;
import com.baidu.bjf.remoting.RemoteClientInfo;
import com.baidu.bjf.remoting.rmi.RmiServiceCreator;
import com.baidu.bjf.remoting.rmi.SmartRmiProxyFactoryBean;
import com.baidu.bjf.remoting.rmi.annotation.RmiProxyInfo;
import com.baidu.bjf.remoting.utils.RemoteClientInfoUtils;
import com.baidu.bjf.transaction.TransactionManager;

/**
 * @author xiemalin
 * @since 1.0.0.0
 */
public class RmiProxyLBAnnotationParser extends AbstractAnnotationParserCallback {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(RmiProxyLBAnnotationParser.class);
    
    private static final Vector<LoadBalanceProxyFactoryBean> exporters = new Vector<LoadBalanceProxyFactoryBean>();
    
    private static final Vector<RemoteClientInfo> proxyClientInfos = new Vector<RemoteClientInfo>();
    
    
    private static final Vector<SmartRmiProxyFactoryBean> proxyFactoryBeans = new Vector<SmartRmiProxyFactoryBean>();
    
    /* (non-Javadoc)
     * @see com.baidu.rigel.service.spring.factory.AnnotationParserCallback#annotationAtType(java.lang.annotation.Annotation, java.lang.Object, java.lang.String, org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    public Object annotationAtType(Annotation t, Object bean, String beanName,
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        return bean;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.spring.factory.AnnotationParserCallback#annotationAtField(java.lang.annotation.Annotation, java.lang.Object, java.lang.String, org.springframework.beans.PropertyValues, org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    public Object annotationAtField(Annotation t, Object bean, String beanName,
            PropertyValues pvs, ConfigurableListableBeanFactory beanFactory, Field field)
            throws BeansException {
        Object o = createObject(t, bean, beanName, pvs, beanFactory, field);
        LOGGER.info("Annotation 'RmiProxyLB' on field for target '" + beanName + "' created");
        return o;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.spring.factory.AnnotationParserCallback#annotationAtMethod(java.lang.annotation.Annotation, java.lang.Object, java.lang.String, org.springframework.beans.PropertyValues, org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    public Object annotationAtMethod(Annotation t, Object bean,
            String beanName, PropertyValues pvs,
            ConfigurableListableBeanFactory beanFactory, Method method) throws BeansException {
        Object o = createObject(t, bean, beanName, pvs, beanFactory, method);
        LOGGER.info("Annotation 'RmiProxyLB' on method for target '" + beanName + "' created");
        return o;
    }
    
    private Object createObject(Annotation t, Object bean, String beanName,
            PropertyValues pvs, ConfigurableListableBeanFactory beanFactory,
            Member member) {
        RmiProxyLBInfo wrapRmiProxyLB = wrapRmiProxyLB(t);
        if (wrapRmiProxyLB != null) {
            LoadBalanceProxyFactoryBean factoryBean;
            factoryBean = createLBFactoryBean(wrapRmiProxyLB, beanFactory, 
                    beanName, pvs, beanFactory, this, member);
            exporters.add(factoryBean);
            return factoryBean.getObject();
        }
        return bean;       
    }
    
    protected RmiProxyLBInfo wrapRmiProxyLB(Annotation t) {
        if (t instanceof RmiProxyLB) {
            RmiProxyLB proxyLB = (RmiProxyLB) t;
            RmiProxyLBInfo info = LBAnnotationWrapper.wrapRmiProxyLB(proxyLB);
            return info;
        }
        return null;
    }
    
    private LoadBalanceProxyFactoryBean createLBFactoryBean(RmiProxyLBInfo t, Object bean,
            String beanName, PropertyValues pvs,
            ConfigurableListableBeanFactory beanFactory, 
            AbstractAnnotationParserCallback callback, Member member) {
        //create load balance instance
        LoadBalanceProxyFactoryBean factoryBean;
        factoryBean = new LoadBalanceProxyFactoryBean();
        
        //parse target RmiProxy
        RmiProxyInfo[] rmis = t.value();
        if (ArrayUtils.isEmpty(rmis)) {
            throw new RuntimeException("Create annotation RmiProxyLB failed due to no target specified");
        }
        
        //get load balance strategy
        String strategyName = t.loadBalanceStrategyBeanName();
        strategyName = callback.parsePlaceholder(strategyName);
        LoadBalanceStrategy strategy = null;
        if (StringUtils.isNotBlank(strategyName)) {
            Object tx = beanFactory.getBean(strategyName);
            strategy = (LoadBalanceStrategy) tx;
        }
        
        Map<String, Integer> lbFactors = new HashMap<String, Integer>(rmis.length);
        Map<String, Object> targets = new HashMap<String, Object>(rmis.length);
        Map<String, String> recoverServiceUrls = new HashMap<String, String>(rmis.length);
        
        boolean isField = false;
        if (member instanceof Field) {
            isField = true;
        }
        
        ConnectionManaged connectionName;
        if (isField) {
            connectionName = getConnectionManagedInfo((Field) member);
        } else {
            connectionName = getConnectionManagedInfo((Method) member);
        }
        
        Class proxyClass = null;
        boolean transactionSupport = false;
        SmartRmiProxyFactoryBean rmiProxyFactoryBean;
        for (RmiProxyInfo rmiProxy : rmis) {
            if (proxyClass == null) {
                proxyClass = rmiProxy.serviceInterface();
            }
            if (!transactionSupport) {
                transactionSupport = rmiProxy.transactionSupport();
            }
            
            rmiProxyFactoryBean = RmiServiceCreator.createRmiProxy(rmiProxy, 
            		bean, beanName, beanFactory, this, connectionName);
            proxyFactoryBeans.add(rmiProxyFactoryBean);
            
            String name = getUniName(rmiProxy);
            lbFactors.put(name, 1); //use default load balance strategy factor
            targets.put(name, rmiProxyFactoryBean.getObject());
            recoverServiceUrls.put(name, callback.parsePlaceholder(rmiProxy.serviceUrl()));
            
            String serviceUrl = callback.parsePlaceholder(rmiProxy.serviceUrl());
            
            if (isField) {
                RemoteClientInfo clientInfo = RemoteClientInfoUtils
                        .createRemoteClientInfo(serviceUrl, rmiProxy, (Field) member);
                proxyClientInfos.add(clientInfo);
            } else {
                RemoteClientInfo clientInfo = RemoteClientInfoUtils
                        .createRemoteClientInfo(serviceUrl, rmiProxy, (Method) member);
                proxyClientInfos.add(clientInfo);
            }
        }
        //set target beans
        factoryBean.setTargetBeans(targets);
        //set service interface
        factoryBean.setServiceInterface(proxyClass);
        
        //check transaction support
        if (transactionSupport) {
            List<String> extraInterfaces = new ArrayList<String>(1);
            extraInterfaces.add(TransactionManager.class.getName());
            factoryBean.setExtraInterfaces(extraInterfaces);
            
            //set Rmi transaction support StrategyInterceptor
            StrategyInterceptor strategyInterceptor;
            strategyInterceptor = new RmiTransactionStrategyInterceptor();
            factoryBean.setStrategyInterceptor(strategyInterceptor);
        }
        
        //failOverInterceptorBeanName
        String failOverName = t.failOverInterceptorBeanName();
        failOverName = callback.parsePlaceholder(failOverName);
        FailOverInterceptor failOverInterceptor = null;
        if (StringUtils.isBlank(failOverName)) {
            //using default
            failOverInterceptor = new RmiFailOverInterceptor();
            ((RmiFailOverInterceptor)failOverInterceptor).setRecoverServiceUrls(recoverServiceUrls);
        } else {
            //get from factory 
            failOverInterceptor = (FailOverInterceptor) beanFactory.getBean(failOverName);
        }
        
        factoryBean.setFailOverInterceptor(failOverInterceptor);
        
        //failover event
        String evenName = t.failOverEventBeanName();
        if (StringUtils.isNotBlank(evenName)) {
            FailOverEvent event = (FailOverEvent) beanFactory.getBean(evenName);
            factoryBean.setFailOverEvent(event);
        }
        
        //load balance strategy
        if (strategy == null) {
            //using default
            strategy = new RoundRobinLoadBalanceStrategy(lbFactors);
        }
        factoryBean.setLoadBalanceStrategy(strategy);
        
        try {
            factoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return factoryBean;
    }
    
    private String getUniName(RmiProxyInfo rmiProxy) {
        String name = rmiProxy.name();
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        return UUID.randomUUID().toString();
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.spring.factory.AnnotationParserCallback#getTypeAnnotation()
     */
    public Class<? extends Annotation> getTypeAnnotation() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.spring.factory.AnnotationParserCallback#getMethodFieldAnnotation()
     */
    public Class<? extends Annotation> getMethodFieldAnnotation() {
        return RmiProxyLB.class;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        for (LoadBalanceProxyFactoryBean export : exporters) {
            try {
                export.destroy();
            } catch (Exception e) {
            }
        }
        for (SmartRmiProxyFactoryBean rmiFactoryBean : proxyFactoryBeans) {
            try {
                rmiFactoryBean.destroy();
            } catch (Exception e) {
            }
        }
        proxyFactoryBeans.clear();
    }

    public void annotationAtTypeAfterStarted(Annotation t, Object bean,
            String beanName, ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
    }

    public static Vector<RemoteClientInfo> getProxyclientinfos() {
        return proxyClientInfos;
    }

    
}
