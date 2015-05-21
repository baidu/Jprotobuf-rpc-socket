/**
 * 
 */
package com.baidu.bjf.lb.remoting.failover.socket;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.baidu.bjf.lb.failover.FailOverInterceptor;

/**
 * Socket fail over intercepter.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class SocketFailOverInterceptor implements FailOverInterceptor {
    
    private Map<String, String> recoverServiceUrls;

    /**
     * format as:  localhost:80
     * 
     * @param recoverServiceUrls
     */
    public void setRecoverServiceUrls(Map<String, String> recoverServiceUrls) {
        this.recoverServiceUrls = recoverServiceUrls;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.lb.failover.FailOverInterceptor#isAvailable(java.lang.Object, java.lang.reflect.Method, java.lang.String)
     */
    public boolean isAvailable(Object o, Method m, String beanKey) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.lb.failover.FailOverInterceptor#isRecover(java.lang.Object, java.lang.reflect.Method, java.lang.String)
     */
    public boolean isRecover(Object o, Method m, String beanKey) {
        Host host = parseHost(beanKey);
        if (host == null) {
            return false;
        }
        
        Socket socket = null;
        try {
            socket = new Socket(host.host, host.port);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see com.baidu.rigel.service.lb.failover.FailOverInterceptor#isDoFailover(java.lang.Throwable, java.lang.String)
     */
    public boolean isDoFailover(Throwable t, String beanKey) {
        return true;
    }

    protected Host parseHost(String beanKey) {
        if (recoverServiceUrls == null) {
            return null;
        }
        String string = recoverServiceUrls.get(beanKey);
        if (StringUtils.isBlank(string)) {
            return null;
        }
        
        String[] splits = string.split(":");
        if (splits == null || splits.length != 2) {
            return null;
        }
        Host host = new Host();
        host.host = splits[0];
        host.port = NumberUtils.toInt(splits[1]);
        return host;
        
    }
    
    private static class Host {
        public String host;
        public int port;
    }
}
