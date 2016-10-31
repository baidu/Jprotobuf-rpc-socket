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
package com.baidu.jprotobuf.pbrpc.client.ha.lb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverEvent;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.FailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.RecoverHeartbeat;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.LoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.RoundRobinLoadBalanceStrategy;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.StrategyInterceptor;

/**
 * a common utility proxy factory bean to support Spring beans load balance support.<br>
 * a example
 * 
 * <pre>
 * {@code
 * <bean id="lbRmiService" class="com.baidu.jprotobuf.pbrpc.client.ha.lb.LoadBalanceProxyFactoryBean">
 *  <property name="serviceInterface" value="com.baidu.rigel.system.subsystem.submodule.service.TestService"></property>
 *  <property name="extraInterfaces">
 *      <list>
 *          <value>com.baidu.rigel.service.remote.support.transaction.TransactionManager</value>
 *      </list>
 *   </property>
 *  <property name="targetBeans">
 *      <map key-type="java.lang.String">
 *          <entry key="helloService1" value-ref="helloServiceClient" />
 *          <entry key="helloService2" value-ref="helloServiceClient2" />
 *      </map>
 *  </property>
 *  <property name="loadBalanceStrategy">
 *      <bean class="com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.RoundRobinLoadBalanceStrategy">
 *          <constructor-arg>
 *              <map key-type="java.lang.String" value-type="java.lang.Integer">
 *                  <entry key="helloService1" value="1" />
 *                  <entry key="helloService2" value="1" />
 *              </map>
 *          </constructor-arg>            
 *      </bean>
 *  </property>
 *  <property name="failOverInterceptor">
 *      <bean class="com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.rmi.RmiFailOverInterceptor">
 *          <property name="recoverServiceUrls">
 *              <map key-type="java.lang.String">
 *                  <entry key="helloService1" value="rmi://localhost:1199/Service" />
 *                  <entry key="helloService2" value="rmi://localhost:1198/Service" />
 *              </map>                    
 *          </property>
 *      </bean>
 *  </property>
 *  <property name="recoverInterval" value="1000"></property>
 * </bean>
 * }
 * </pre>
 * 
 * @author xiemalin
 * @since 2.17
 */
public class LoadBalanceProxyFactoryBean extends ServiceMultiInterfaceAccessor implements BeanClassLoaderAware,
        FactoryBean, InitializingBean, MethodInterceptor, DisposableBean, BeanNameAware {
    
    /** The Constant DEFAULT_LB_FACTOR. */
    private static final int DEFAULT_LB_FACTOR = 1;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(LoadBalanceProxyFactoryBean.class.getName());

    /** The bean class loader. */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /** The target beans. */
    private Map<String, Object> targetBeans;

    /** The service proxy. */
    private Object serviceProxy;

    /** The load balance strategy. */
    private LoadBalanceStrategy loadBalanceStrategy;

    /** The fail over interceptor. */
    private FailOverInterceptor failOverInterceptor;

    /** The failed factory beans. */
    private Map<String, FactoryBeanInvokeInfo> failedFactoryBeans =
            new ConcurrentHashMap<String, FactoryBeanInvokeInfo>();

    /** The recover heartbeat. */
    private RecoverHeartbeat recoverHeartbeat;

    /** The exe. */
    private ExecutorService exe;

    /** The fail over event. */
    private FailOverEvent failOverEvent;

    /** The strategy interceptor. */
    private StrategyInterceptor strategyInterceptor;

    /** The heart beat. */
    private boolean heartBeat = true;

    /** The recover interval. */
    private long recoverInterval = 1000L;

    /** The bean name. */
    private String beanName = "";
    
    /** The lastest exception. */
    private Throwable lastestException;

    /**
     * Sets the fail over event.
     *
     * @param failOverEvent the new fail over event
     */
    public void setFailOverEvent(FailOverEvent failOverEvent) {
        this.failOverEvent = failOverEvent;
    }

    /**
     * Sets the target beans.
     *
     * @param targetFactoryBeans the target factory beans
     */
    public void setTargetBeans(Map<String, Object> targetFactoryBeans) {
        this.targetBeans = targetFactoryBeans;
    }

    /**
     * add target bean.
     *
     * @param key the key
     * @param targetBean the target bean
     */
    public synchronized void addTargetBean(String key, Object targetBean) {
        if (this.targetBeans == null) {
            this.targetBeans = new ConcurrentHashMap<String, Object>();
        }
        targetBeans.put(key, targetBean);
    }

    /**
     * Sets the load balance strategy.
     *
     * @param loadBalanceStrategy the new load balance strategy
     */
    public void setLoadBalanceStrategy(LoadBalanceStrategy loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    /**
     * Gets the fail over interceptor.
     *
     * @return the fail over interceptor
     */
    public FailOverInterceptor getFailOverInterceptor() {
        return failOverInterceptor;
    }

    /**
     * Sets the fail over interceptor.
     *
     * @param failOverInterceptor the new fail over interceptor
     */
    public void setFailOverInterceptor(FailOverInterceptor failOverInterceptor) {
        this.failOverInterceptor = failOverInterceptor;
    }

    /**
     * Checks if is fail over.
     *
     * @return true, if is fail over
     */
    public boolean isFailOver() {
        return failOverInterceptor != null;
    }

    /**
     * Checks if is assignable from.
     *
     * @param interfaces the interfaces
     * @param clazz the clazz
     * @return true, if is assignable from
     */
    private boolean isAssignableFrom(List<Class> interfaces, Class clazz) {
        if (interfaces == null) {
            return true;
        }
        for (Class c : interfaces) {
            if (!c.isAssignableFrom(clazz)) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    // /---- InitializingBean implement
    public void afterPropertiesSet() throws Exception {
        if (getServiceInterface() == null) {
            throw new IllegalArgumentException("Property 'serviceInterfaces' is required");
        }

        // check all the target Factory beans must valid
        if (targetBeans == null) {
            throw new IllegalArgumentException("Property 'targetFactoryBeans' is required");
        }
        for (Map.Entry<String, Object> entry : targetBeans.entrySet()) {
            Object o = entry.getValue();
            if (!getServiceInterface().isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException("target facotry bean class '" + entry.getKey()
                        + "' must implement serviceInterface");
            }

            if (!isAssignableFrom(getExtraServiceInterfaces(), o.getClass())) {
                throw new IllegalArgumentException("target facotry bean class '" + entry.getKey()
                        + "' must implement all the extraInterfaces ");
            }
        }
        
        
        ProxyFactory pf = new ProxyFactory(getServiceInterface(), this);

        if (getExtraServiceInterfaces() != null) {
            for (Class clazz : getExtraServiceInterfaces()) {
                pf.addInterface(clazz);
            }

        }
        this.serviceProxy = pf.getProxy(getBeanClassLoader());

        // default using Random strategy
        if (loadBalanceStrategy == null) {
            Map<String, Integer> lbFactors = new HashMap<String, Integer>();

            for (String key : targetBeans.keySet()) {
                lbFactors.put(key, DEFAULT_LB_FACTOR);
            }
            loadBalanceStrategy = new RoundRobinLoadBalanceStrategy(lbFactors);
        } else {
            // valid balance strategy targets
            Set<String> targets = loadBalanceStrategy.getTargets();
            if (targets == null) {
                targets = new HashSet<String>();
            }
            for (String key : targets) {
                if (!targetBeans.containsKey(key)) {
                    throw new IllegalArgumentException("the target key '" + key + "' of loadBalanceStrategy is invalid");
                }
            }
        }

        // for synchronized lock
        targetBeans = Collections.synchronizedMap(targetBeans);

        if (!isFailOver()) {
            LOGGER.log(Level.WARNING,
                    "LoadBalanceProxy is shut down failover action due to not set FailOverInterceptor");
        }
    }

    // /---- FactoryBean implement

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() {
        return this.serviceProxy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return getServiceInterface();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    // /--------- BeanClassLoaderAware implement

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
     */
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /**
     * Gets the bean class loader.
     *
     * @return the bean class loader
     */
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    /**
     * Failed target.
     *
     * @param bean the bean
     * @param invocation the invocation
     * @param beanKey the bean key
     * @throws Throwable the throwable
     */
    private void failedTarget(Object bean, MethodInvocation invocation, String beanKey) throws Throwable {
        loadBalanceStrategy.removeTarget(beanKey);
        // add to failed target list
        FactoryBeanInvokeInfo info = new FactoryBeanInvokeInfo(bean, getMethod(bean, invocation), beanKey);
        failedFactoryBeans.put(beanKey, info);
        executeHeartBeat(); // execute heart beat for factory bean recover
                            // detecting

        if (failOverEvent != null) {
            failOverEvent.onTargetFailed(beanKey, bean, invocation);
        }
        if (strategyInterceptor != null) {
            strategyInterceptor.onTargetFailed(beanKey, bean, invocation);
        }
    }

    /**
     * do strategy election.
     *
     * @param invocation the invocation
     * @return the string
     */
    private String elect(MethodInvocation invocation) {
        String key = null;
        if (strategyInterceptor != null) {
            strategyInterceptor.beforeElection(loadBalanceStrategy, invocation);
            if (!strategyInterceptor.isDoElection(invocation)) {
                key = strategyInterceptor.elect(invocation);
            }
        }
        if (key == null) {
            try {
                key = loadBalanceStrategy.elect();
            } catch (Exception e) {
                String message = "A error found: " + e.getMessage() + "";
                if (lastestException != null) {
                    message += " with last exception message:" + lastestException.getMessage();
                }
                throw new RuntimeException(message, e);
            }
        }

        if (strategyInterceptor != null) {
            strategyInterceptor.afterElection(key, invocation);
        }
        return key;
    }

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    // /---- MethodInterceptor implement
    public Object invoke(MethodInvocation invocation) throws Throwable {
        int maxTry = loadBalanceStrategy.getTargets().size();
        return invokeWithMaxTry(invocation, maxTry);

    }
    
    /**
     * Invoke with max try.
     *
     * @param invocation the invocation
     * @param maxTry the max try
     * @return the object
     * @throws Throwable the throwable
     */
    public Object invokeWithMaxTry(MethodInvocation invocation, int maxTry) throws Throwable {
        String beanKey = elect(invocation);
        Object bean = targetBeans.get(beanKey);
        if (isFailOver()) { // support fail over
            boolean isAvailable;
            try {
                isAvailable = failOverInterceptor.isAvailable(bean, getMethod(bean, invocation), beanKey);
            } catch (Exception e) {
                isAvailable = false;
            }
            if (!isAvailable) {
                failedTarget(bean, invocation, beanKey);
                // using recursion to do fail over action
                return invokeWithMaxTry(invocation, maxTry);
            }
        }

        if (bean != null) {
            try {
                return doInvoke(bean, invocation);
            } catch (Throwable e) {
                Throwable t = getRealException(e);
                lastestException = t;
                if (isFailOver() && failOverInterceptor.isDoFailover(t, beanKey)) {
                    LOGGER.log(Level.SEVERE,
                            "do failover action due to last access throws exception: " + t.getLocalizedMessage());
                    failedTarget(bean, invocation, beanKey);
                    maxTry--;
                    if (maxTry < 1) { // reach the max try times
                        throw t;
                    }
                    return invokeWithMaxTry(invocation, maxTry); // do fail over action
                }
                throw t;
            }

        }

        throw new NullPointerException("target bean is null");
    }

    /**
     * Gets the real exception.
     *
     * @param t the t
     * @return the real exception
     */
    private Throwable getRealException(Throwable t) {
        do {
            if (t instanceof UndeclaredThrowableException) {
                t = ((UndeclaredThrowableException) t).getCause();
            }
            // get real exception
            if (t instanceof InvocationTargetException) {
                t = ((InvocationTargetException) t).getTargetException();
            }
        } while ((t instanceof UndeclaredThrowableException) || (t instanceof InvocationTargetException));

        return t;

    }

    /**
     * Do invoke.
     *
     * @param bean the bean
     * @param invocation the invocation
     * @return the object
     * @throws Throwable the throwable
     */
    // --- others
    private Object doInvoke(Object bean, MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Method m = getMethod(bean, invocation);
        return m.invoke(bean, args);
    }

    /**
     * Gets the method.
     *
     * @param bean the bean
     * @param invocation the invocation
     * @return the method
     * @throws Throwable the throwable
     */
    private Method getMethod(Object bean, MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        if (bean == null) {
            System.out.println("error");
        }
        Class<? extends Object> cls = bean.getClass();
        Method m = cls.getMethod(methodName, invocation.getMethod().getParameterTypes());
        return m;
    }

    /**
     * Gets the failed factory beans.
     *
     * @return the failed factory beans
     */
    public Map<String, FactoryBeanInvokeInfo> getFailedFactoryBeans() {
        return failedFactoryBeans;
    }

    /**
     * Recover factory bean.
     *
     * @param key the key
     */
    public synchronized void recoverFactoryBean(String key) {
        if (failedFactoryBeans.containsKey(key)) {
            failedFactoryBeans.remove(key);

            loadBalanceStrategy.recoverTarget(key);

            if (failOverEvent != null) {
                failOverEvent.onTargetRecover(key);
            }
            if (strategyInterceptor != null) {
                strategyInterceptor.onTargetRecover(key);
            }
        }
    }

    /**
     * Checks for factory bean failed.
     *
     * @return true, if successful
     */
    public synchronized boolean hasFactoryBeanFailed() {
        return !failedFactoryBeans.isEmpty();
    }

    /**
     * Execute heart beat.
     */
    private synchronized void executeHeartBeat() {
        if (!isHeartBeat()) { // if close heart beat manually
            return;
        }
        if (exe == null || exe.isShutdown()) {
            exe = Executors.newFixedThreadPool(1, new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    return new Thread(r, "loadbalance-" + beanName);
                }
            });
        }
        if (recoverHeartbeat == null) {
            recoverHeartbeat = new RecoverHeartbeat(this);
            exe.execute(recoverHeartbeat);
        } else {
            if (!recoverHeartbeat.isRuning()) {
                exe.execute(recoverHeartbeat);
            }
        }
    }

    /**
     * The Class FactoryBeanInvokeInfo.
     */
    public static class FactoryBeanInvokeInfo {
        
        /** The bean. */
        private Object bean;
        
        /** The m. */
        private Method m;
        
        /** The bean key. */
        private String beanKey;

        /**
         * Instantiates a new factory bean invoke info.
         *
         * @param bean the bean
         * @param m the m
         * @param beanKey the bean key
         */
        public FactoryBeanInvokeInfo(Object bean, Method m, String beanKey) {
            super();
            this.bean = bean;
            this.m = m;
            this.beanKey = beanKey;
        }

        /**
         * Gets the bean.
         *
         * @return the bean
         */
        public Object getBean() {
            return bean;
        }

        /**
         * Gets the invocation.
         *
         * @return the invocation
         */
        public Method getInvocation() {
            return m;
        }

        /**
         * Gets the bean key.
         *
         * @return the bean key
         */
        public String getBeanKey() {
            return beanKey;
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    // /---- DisposableBean
    public void destroy() throws Exception {
        if (recoverHeartbeat != null) {
            recoverHeartbeat.close();
        }
        if (exe != null) {
            exe.shutdown();
            exe = null;
        }
    }

    /**
     * Sets the strategy interceptor.
     *
     * @param strategyInterceptor the new strategy interceptor
     */
    public void setStrategyInterceptor(StrategyInterceptor strategyInterceptor) {
        this.strategyInterceptor = strategyInterceptor;
    }

    /**
     * Sets the recover interval.
     *
     * @param recoverInterval the new recover interval
     */
    public void setRecoverInterval(long recoverInterval) {
        this.recoverInterval = recoverInterval;
    }

    /**
     * Gets the recover interval.
     *
     * @return the recover interval
     */
    public long getRecoverInterval() {
        return recoverInterval;
    }

    /**
     * Checks if is heart beat.
     *
     * @return true, if is heart beat
     */
    protected boolean isHeartBeat() {
        return heartBeat;
    }

    /**
     * Sets the heart beat.
     *
     * @param heartBeat the new heart beat
     */
    public void setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
        if (!heartBeat) {
            LOGGER.log(Level.WARNING, "LoadBalance heartbeat is set to disabled");
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;

    }
}
