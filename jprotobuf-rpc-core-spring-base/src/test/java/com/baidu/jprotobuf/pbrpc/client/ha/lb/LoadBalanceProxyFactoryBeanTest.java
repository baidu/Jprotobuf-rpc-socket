/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.jprotobuf.pbrpc.client.ha.lb;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.test.Greet;
import com.baidu.jprotobuf.pbrpc.client.test.SimpleGreet;

/**
 * The Class LoadBalanceProxyFactoryBeanTest.
 *
 * @author xiemalin
 * @since 3.5.22
 */
public class LoadBalanceProxyFactoryBeanTest {

    /**
     * New load balance proxy factory bean.
     *
     * @param serviceInterface the service interface
     * @param targets the targets
     * @return the load balance proxy factory bean
     */
    private LoadBalanceProxyFactoryBean newLoadBalanceProxyFactoryBean(Class serviceInterface,
            Map<String, Object> targets) {
        LoadBalanceProxyFactoryBean loadBalanceProxyFactoryBean = new LoadBalanceProxyFactoryBean();

        loadBalanceProxyFactoryBean.setServiceInterface(serviceInterface);
        loadBalanceProxyFactoryBean.setTargetBeans(targets);

        return loadBalanceProxyFactoryBean;
    }

    /**
     * Test null service interface.
     */
    @Test
    public void testNullServiceInterface() {
        LoadBalanceProxyFactoryBean loadBalanceProxyFactoryBean = newLoadBalanceProxyFactoryBean(null, null);

        try {
            loadBalanceProxyFactoryBean.afterPropertiesSet();
            Assert.fail("loadBalanceProxyFactoryBean with null service interface property should throw exception.");
        } catch (Exception e) {
            Assert.assertNotNull("exception should not null.", e);
        }
    }

    /**
     * Test null target beans.
     */
    @Test
    public void testNullTargetBeans() {
        LoadBalanceProxyFactoryBean loadBalanceProxyFactoryBean =
                newLoadBalanceProxyFactoryBean(Greet.class, null);

        try {
            loadBalanceProxyFactoryBean.afterPropertiesSet();
            Assert.fail("loadBalanceProxyFactoryBean with null 'targetBeans' property should throw exception.");
        } catch (Exception e) {
            Assert.assertNotNull("exception should not null.", e);
        }
    }
    
    @Test
    public void testWithOnlyOneElementForTargetBeans() {
        Map<String, Object> targetBeans = new HashMap<String, Object>();
        targetBeans.put("greet", new SimpleGreet());
        
        LoadBalanceProxyFactoryBean loadBalanceProxyFactoryBean =
                newLoadBalanceProxyFactoryBean(Greet.class, targetBeans);
        
        
        try {
            loadBalanceProxyFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
