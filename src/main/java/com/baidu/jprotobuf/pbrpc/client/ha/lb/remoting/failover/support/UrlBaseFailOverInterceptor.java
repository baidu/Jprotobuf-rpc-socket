/**
 * 
 */
package com.baidu.bjf.lb.remoting.failover.support;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baidu.bjf.lb.failover.FailOverInterceptor;

/**
 * @author xiemalin
 * @since 1.0.0.0
 */
public abstract class UrlBaseFailOverInterceptor implements FailOverInterceptor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(UrlBaseFailOverInterceptor.class);
	private Map<String, String> recoverServiceUrls;

    /**
	 * @return the recoverServiceUrls
	 */
	protected Map<String, String> getRecoverServiceUrls() {
		return recoverServiceUrls;
	}

	/**
	 * @param recoverServiceUrls the recoverServiceUrls to set
	 */
	public void setRecoverServiceUrls(Map<String, String> recoverServiceUrls) {
		this.recoverServiceUrls = recoverServiceUrls;
	}

	/*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.service.lb.failover.FailOverInterceptor#isRecover(java
     * .lang.Object, java.lang.reflect.Method, java.lang.String)
     */
    public boolean isRecover(Object o, Method m, String beanKey) {
        if (recoverServiceUrls == null) {
            return false;
        }
        String serviceUrl = recoverServiceUrls.get(beanKey);
        try {
            URLConnection connection;
            connection = new URL(serviceUrl).openConnection();
            connection.connect();
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection) connection).disconnect();
            }
        } catch (Exception e) {
            LOGGER.debug(e.getMessage(), e);
            LOGGER.warn("Url service [" + serviceUrl + "] recover failed due to: " + e.getMessage());
            return false;
        }
        return true;
    }
}
