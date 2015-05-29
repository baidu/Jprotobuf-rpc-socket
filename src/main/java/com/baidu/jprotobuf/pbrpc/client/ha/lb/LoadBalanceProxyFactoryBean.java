/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.client.ha.lb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
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
 * @since 1.0.0.0
 */
public class LoadBalanceProxyFactoryBean extends ServiceMultiInterfaceAccessor implements BeanClassLoaderAware,
        FactoryBean, InitializingBean, MethodInterceptor, DisposableBean, BeanNameAware {
    /**
     * 
     */
    private static final int DEFAULT_LB_FACTOR = 1;

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(LoadBalanceProxyFactoryBean.class.getName());

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private Map<String, Object> targetBeans;

    private Object serviceProxy;

    private LoadBalanceStrategy loadBalanceStrategy;

    private FailOverInterceptor failOverInterceptor;

    private Map<String, FactoryBeanInvokeInfo> failedFactoryBeans =
            new ConcurrentHashMap<String, FactoryBeanInvokeInfo>();

    private RecoverHeartbeat recoverHeartbeat;

    private ExecutorService exe;

    private FailOverEvent failOverEvent;

    private StrategyInterceptor strategyInterceptor;

    private boolean heartBeat = true;

    private long recoverInterval = 1000L;

    private String beanName = "";
    
    private Throwable lastestException;

    public void setFailOverEvent(FailOverEvent failOverEvent) {
        this.failOverEvent = failOverEvent;
    }

    /**
     * 
     * @param targetFactoryBeans
     */
    public void setTargetBeans(Map<String, Object> targetFactoryBeans) {
        this.targetBeans = targetFactoryBeans;
    }

    /**
     * add target bean
     * 
     * @param key
     * @param targetBean
     */
    public synchronized void addTargetBean(String key, Object targetBean) {
        if (this.targetBeans == null) {
            this.targetBeans = new ConcurrentHashMap<String, Object>();
        }
        targetBeans.put(key, targetBean);
    }

    /**
     * 
     * @param loadBalanceStrategy
     */
    public void setLoadBalanceStrategy(LoadBalanceStrategy loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    /**
     * @return the failOverInterceptor
     */
    public FailOverInterceptor getFailOverInterceptor() {
        return failOverInterceptor;
    }

    /**
     * @param failOverInterceptor the failOverInterceptor to set
     */
    public void setFailOverInterceptor(FailOverInterceptor failOverInterceptor) {
        this.failOverInterceptor = failOverInterceptor;
    }

    /**
     * @return
     */
    public boolean isFailOver() {
        return failOverInterceptor != null;
    }

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
            if (targets == null || targets.isEmpty()) {
                throw new IllegalArgumentException("the targets of loadBalanceStrategy can not be empty");
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

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /**
     * Return the ClassLoader that this accessor operates in, to be used for deserializing and for generating proxies.
     */
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

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
     * do strategy election
     * 
     * @param invocation
     * @return
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

    // /---- MethodInterceptor implement
    public Object invoke(MethodInvocation invocation) throws Throwable {
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
                return invoke(invocation);
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
                    return invoke(invocation); // do fail over action
                }
                throw t;
            }

        }

        throw new NullPointerException("target bean is null");
    }

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

    // --- others
    private Object doInvoke(Object bean, MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Method m = getMethod(bean, invocation);
        return m.invoke(bean, args);
    }

    private Method getMethod(Object bean, MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Method m = bean.getClass().getMethod(methodName, invocation.getMethod().getParameterTypes());
        return m;
    }

    /**
     * @return the failedFactoryBeans
     */
    public Map<String, FactoryBeanInvokeInfo> getFailedFactoryBeans() {
        return failedFactoryBeans;
    }

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

    public synchronized boolean hasFactoryBeanFailed() {
        return !failedFactoryBeans.isEmpty();
    }

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

    public static class FactoryBeanInvokeInfo {
        private Object bean;
        private Method m;
        private String beanKey;

        public FactoryBeanInvokeInfo(Object bean, Method m, String beanKey) {
            super();
            this.bean = bean;
            this.m = m;
            this.beanKey = beanKey;
        }

        /**
         * @return the factoryBean
         */
        public Object getBean() {
            return bean;
        }

        /**
         * @return the invocation
         */
        public Method getInvocation() {
            return m;
        }

        /**
         * @return the beanKey
         */
        public String getBeanKey() {
            return beanKey;
        }
    }

    // /---- DisposableBean
    public void destroy() throws Exception {
        if (recoverHeartbeat != null) {
            recoverHeartbeat.close();
        }
        if (exe != null) {
            exe.shutdown();
            exe = null;
        }

        if (loadBalanceStrategy != null) {
            loadBalanceStrategy.close();
        }
    }

    /**
     * @param strategyInterceptor the strategyInterceptor to set
     */
    public void setStrategyInterceptor(StrategyInterceptor strategyInterceptor) {
        this.strategyInterceptor = strategyInterceptor;
    }

    /**
     * @param recoverInterval the recoverInterval to set
     */
    public void setRecoverInterval(long recoverInterval) {
        this.recoverInterval = recoverInterval;
    }

    /**
     * @return the recoverInterval
     */
    public long getRecoverInterval() {
        return recoverInterval;
    }

    /**
     * @return the heartBeat
     */
    protected boolean isHeartBeat() {
        return heartBeat;
    }

    /**
     * @param heartBeat the heartBeat to set
     */
    public void setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
        if (!heartBeat) {
            LOGGER.log(Level.WARNING, "LoadBalance heartbeat is set to disabled");
        }
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;

    }
}
