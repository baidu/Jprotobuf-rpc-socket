/**
 * 
 */
package com.baidu.bjf.lb.remoting.failover.rmi;

import java.lang.reflect.Method;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiClientInterceptorUtils;

import com.baidu.bjf.lb.failover.FailOverInterceptor;

/**
 * RMI load balance failover intercepter implmentation.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class RmiFailOverInterceptor implements FailOverInterceptor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(RmiFailOverInterceptor.class);

    private Map<String, String> recoverServiceUrls;

    public void setRecoverServiceUrls(Map<String, String> recoverServiceUrls) {
        this.recoverServiceUrls = recoverServiceUrls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see enhance.lb.failover.FailOverInterceptor#isAvailable(
     * java.lang.Object, java.lang.reflect.Method)
     */
    public boolean isAvailable(Object o, Method m, String beanKey) {
        return true;
    }
    
    public boolean isDoFailover(Throwable t, String beanKey) {
    	Throwable temp = t;
    	while (temp != null) {
    		if (needDoFailover(temp, beanKey)) {
    			return true;
    		} else {
    			temp = temp.getCause();
    		}
    	}
    	return false;
    }

    private boolean needDoFailover(Throwable t, String beanKey) {
        if (t instanceof RemoteLookupFailureException) {
            return true;
        }
        if (t instanceof RemoteConnectFailureException) {
            return true;
        }
        
        if (t instanceof RemoteAccessException) {
        	return true;
        }

        if (t instanceof RemoteException) {
            return RmiClientInterceptorUtils
                    .isConnectFailure((RemoteException) t);
        }
        return false;
    }

    public boolean isRecover(Object o, Method m, String beanKey) {
        if (recoverServiceUrls == null) {
            return false;
        }
        String serviceUrl = recoverServiceUrls.get(beanKey);
        try {
            Naming.lookup(serviceUrl);
        } catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
            LOGGER.warn("Rmi service [" + serviceUrl + "] recover failed due to: " + e.getMessage());
            return false;
        }
        return true;
    }

}
