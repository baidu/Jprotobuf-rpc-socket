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
package com.baidu.jprotobuf.pbrpc.client.ha.lb.failover;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baidu.jprotobuf.pbrpc.client.ServiceUrlAccessible;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * Socket fail over intercepter.
 * 
 * @author xiemalin
 * @since 2.16
 */
public class SocketFailOverInterceptor implements FailOverInterceptor {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SocketFailOverInterceptor.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.service.lb.failover.FailOverInterceptor#isAvailable(java .lang.Object,
     * java.lang.reflect.Method, java.lang.String)
     */
    public boolean isAvailable(Object o, Method m, String beanKey) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.service.lb.failover.FailOverInterceptor#isRecover(java .lang.Object,
     * java.lang.reflect.Method, java.lang.String)
     */
    public boolean isRecover(Object o, Method m, String beanKey) {
        if (!(o instanceof ServiceUrlAccessible)) {
            LOGGER.log(Level.SEVERE, "Invalid target object to recover, should be implements '"
                    + ServiceUrlAccessible.class.getName() + "'");
            return false;
        }
        
        String serviceUrl = ((ServiceUrlAccessible) o).getServiceUrl();

        Host host = parseHost(serviceUrl);
        if (host == null) {
            return false;
        }

        Socket socket = null;
        try {
            socket = new Socket(host.host, host.port);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Recover socket test for host '" + host.host + "' and port '" + host.port
                    + "' failed. message:" + e.getMessage());

            return false;
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                // need to care this exception
                LOGGER.log(Level.FINEST, e.getMessage(), e.getCause());
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.service.lb.failover.FailOverInterceptor#isDoFailover( java.lang.Throwable, java.lang.String)
     */
    public boolean isDoFailover(Throwable t, String beanKey) {
        return true;
    }

    /**
     * Parses the host.
     *
     * @param serviceUrl the service url
     * @return the host
     */
    protected Host parseHost(String serviceUrl) {
        if (StringUtils.isBlank(serviceUrl)) {
            return null;
        }

        String[] splits = serviceUrl.split(":");
        if (splits == null || splits.length != 2) {
            return null;
        }
        Host host = new Host();
        host.host = splits[0];
        host.port = StringUtils.toInt(splits[1]);
        return host;

    }

    /**
     * The Class Host.
     */
    private static class Host {
        
        /** The host. */
        public String host;
        
        /** The port. */
        public int port;
    }
}
