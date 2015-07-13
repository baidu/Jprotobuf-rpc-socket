/*
 * Copyright 2002-2014 the original author or authors.
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
package com.baidu.pbrpc.register.redis;

import java.util.logging.Logger;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import com.baidu.jprotobuf.pbrpc.utils.Pool;

/**
 * Connection pool for jedis.
 * 
 * @author xiemalin
 * @since 1.0
 */
public class JedisPool extends Pool<Jedis> {
    
    private static final Logger LOG = Logger.getLogger(JedisPool.class.getName());

    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host) {
        this(poolConfig, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, null);
    }

    public JedisPool(String host, int port) {
        super(new GenericObjectPoolConfig(), new JedisFactory(host, port, Protocol.DEFAULT_TIMEOUT, null));
    }

    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port, int timeout,
            final String password) {
        super(poolConfig, new JedisFactory(host, port, timeout, password));
    }

    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port) {
        this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null);
    }

    public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int timeout) {
        this(poolConfig, host, port, timeout, null);
    }

    /**
     * PoolableObjectFactory custom impl.
     */
    private static class JedisFactory extends BasePooledObjectFactory<Jedis> {
        /**
         * 
         */
        private static final String REDIS_PING_KEY = "____pbrpc_ping_key_____";
        private final String host;
        private final int port;
        private final int timeout;
        private final String password;

        public JedisFactory(final String host, final int port, final int timeout, final String password) {
            super();
            this.host = host;
            this.port = port;
            this.timeout = (timeout > 0) ? timeout : -1;
            this.password = password;
        }

        public void destroyObject(PooledObject<Jedis> p) throws Exception {
            final Jedis jedis = p.getObject();
            if (jedis.isConnected()) {
                try {
                    try {
                        jedis.quit();
                    } catch (Exception e) {
                        LOG.warning(e.getMessage());
                    }
                    jedis.disconnect();
                } catch (Exception e) {
                    LOG.warning(e.getMessage());
                }
            }
        }

        /**
         * This implementation always returns {@code true}.
         * 
         * @param p ignored
         * 
         * @return {@code true}
         */
        @Override
        public boolean validateObject(PooledObject<Jedis> p) {
            final Jedis jedis = p.getObject();
            try {
                return jedis.isConnected() && jedis.set(REDIS_PING_KEY, "") != null;
            } catch (final Exception e) {
                return false;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
         */
        @Override
        public Jedis create() throws Exception {
            final Jedis jedis;
            if (timeout > 0) {
                jedis = new Jedis(this.host, this.port, this.timeout);
            } else {
                jedis = new Jedis(this.host, this.port);
            }

            return jedis;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.commons.pool2.BasePooledObjectFactory#wrap(java.lang.Object)
         */
        @Override
        public PooledObject<Jedis> wrap(Jedis jedis) {
            jedis.connect();
            if (null != this.password) {
                jedis.auth(this.password);
            }
            return new DefaultPooledObject<Jedis>(jedis);
        }

    }
}
