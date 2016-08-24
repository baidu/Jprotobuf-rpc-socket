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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.jprotobuf.pbrpc.ErrorDataException;
import com.baidu.jprotobuf.pbrpc.transport.handler.ErrorCodes;

/**
 * Dummy all failover action of timeout exception.
 *
 * @author xiemalin
 * @since 3.1.9
 */
public class TimeoutIgnoredSocketFailOverInterceptor extends SocketFailOverInterceptor {
    
    /** log this class. */
    protected static final Log LOGGER = LogFactory.getLog(TimeoutIgnoredSocketFailOverInterceptor.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor#isDoFailover(java.lang.Throwable,
     * java.lang.String)
     */
    @Override
    public boolean isDoFailover(Throwable t, String beanKey) {
        if (t instanceof ErrorDataException) {
            ErrorDataException errorDataException = (ErrorDataException) t;
            int errorCode = errorDataException.getErrorCode();
            // only not read time out error should do fail over action 
            boolean doFailover = ErrorCodes.ST_READ_TIMEOUT != errorCode;
            if (!doFailover && LOGGER.isInfoEnabled()) {
                LOGGER.info("Found timeout exception of ErrorDataException in failoverinterceptor, "
                        + "will not do failover now.");
            }
            return doFailover;
        }
        
        return super.isDoFailover(t, beanKey);
    }
}
