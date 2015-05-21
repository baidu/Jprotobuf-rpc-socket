/**
 * 
 */
package com.baidu.bjf.lb.remoting.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import com.baidu.bjf.lb.strategy.LoadBalanceStrategy;
import com.baidu.bjf.lb.strategy.StrategyInterceptor;
import com.baidu.bjf.transaction.TransactionManager;

/**
 * Remote transaction support load balance strategy intercepter.
 * 
 * @author xiemalin
 * @see StrategyInterceptor
 * @since 1.0.0.0
 */
public class RemoteTransactionStrategyInterceptor implements StrategyInterceptor {

    private static final ThreadLocal<Map<Object, String>> beanKeys = new ThreadLocal<Map<Object, String>>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.strategy.StrategyInterceptor#afterElection
     * (java.lang.String, org.aopalliance.intercept.MethodInvocation)
     */
    public void afterElection(String electKey, MethodInvocation invocation) {

        Class clazz = invocation.getMethod().getDeclaringClass();

        // if clazz is assign from MethodInterceptor
        if (MethodInterceptor.class.isAssignableFrom(clazz)) {
            MethodInvocation mi = extractMethodInvocation(invocation);
            if (mi != null) {
                afterElection(electKey, mi);
            }
        } else if (TransactionManager.class.isAssignableFrom(clazz)) {
            String methodName = invocation.getMethod().getName();
            // commit or rollback transaction
            if (TransactionManager.ROLLBACK_METHODNAME.equals(methodName)
                    || TransactionManager.COMMIT_METHODNAME.equals(methodName)) {
                if (beanKeys.get() != null && !beanKeys.get().isEmpty()) {
                    Map<Object, String> keys = beanKeys.get();
                    keys.remove(getTarget(invocation));
                    beanKeys.set(keys);
                }
            }
        }
    }

    private Object getTarget(MethodInvocation invocation) {
        if (invocation instanceof ReflectiveMethodInvocation) {
            return ((ReflectiveMethodInvocation) invocation).getProxy();
        }
        return invocation.getClass().getDeclaringClass().toString();
    }

    private MethodInvocation extractMethodInvocation(MethodInvocation invocation) {
        Object[] args = invocation.getArguments();
        if (args != null && args.length == 1
                && (args[0] instanceof MethodInvocation)) {
            return (MethodInvocation) args[0];
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.strategy.StrategyInterceptor#beforeElection
     * (org.aopalliance.intercept.MethodInvocation)
     */
    public void beforeElection(LoadBalanceStrategy loadBalanceStrategy,
            MethodInvocation invocation) {
        Class clazz = invocation.getMethod().getDeclaringClass();
        // if clazz is assign from MethodInterceptor
        if (MethodInterceptor.class.isAssignableFrom(clazz)) {
            MethodInvocation mi = extractMethodInvocation(invocation);
            if (mi != null) {
                beforeElection(loadBalanceStrategy, mi);
            }
        } else if (TransactionManager.class.isAssignableFrom(clazz)) {
            String methodName = invocation.getMethod().getName();
            // begin transaction
            if (TransactionManager.BEGIN_TRANSACTION_METHODNAME
                    .equals(methodName)) {
                if (beanKeys.get() == null) {
                    beanKeys.set(new HashMap<Object, String>());
                }
                Map<Object, String> keys = beanKeys.get();
                String key = loadBalanceStrategy.elect();
                keys.put(getTarget(invocation), key);
                beanKeys.set(keys);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.service.lb.strategy.StrategyInterceptor#elect()
     */
    public String elect(MethodInvocation invocation) {
        return beanKeys.get().get(getTarget(invocation));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.strategy.StrategyInterceptor#isDoElection(
     * org.aopalliance.intercept.MethodInvocation)
     */
    public boolean isDoElection(MethodInvocation invocation) {
        if (beanKeys.get() == null) {
            return true;
        }
        return !beanKeys.get().containsKey(getTarget(invocation));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.failover.FailOverEvent#onTargetFailed(java
     * .lang.String)
     */
    public synchronized void onTargetFailed(String targetName, Object bean,
            MethodInvocation invocation) {
        if (beanKeys.get() != null) {
            Map<Object, String> temp = new HashMap<Object, String>(beanKeys
                    .get());
            Set<Object> keys = temp.keySet();
            String beanKey;
            String methodName = invocation.getMethod().getName();
            for (Object key : keys) {
                beanKey = beanKeys.get().get(key);
                if (beanKey.equals(targetName)) {
                    beanKeys.get().remove(key);
                    if (!TransactionManager.BEGIN_TRANSACTION_METHODNAME
                            .equals(methodName)) {
                        // here in transaction action we should broken the
                        // invoke
                        throw new RuntimeException(
                                "failover action denied, due to not support in transaction.");
                    }

                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.failover.FailOverEvent#onTargetRecover(java
     * .lang.String)
     */
    public void onTargetRecover(String targetName) {

    }

}
