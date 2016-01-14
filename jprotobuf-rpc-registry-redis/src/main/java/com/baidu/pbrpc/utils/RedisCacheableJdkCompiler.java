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
package com.baidu.pbrpc.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.baidu.bjf.remoting.protobuf.utils.compiler.CacheableJdkCompiler;
import com.baidu.bjf.remoting.protobuf.utils.compiler.Compiler;
import com.baidu.bjf.remoting.protobuf.utils.compiler.JdkCompiler;
import com.baidu.pbrpc.register.redis.JedisPool;
import com.baidu.pbrpc.register.redis.RedisClient;
import com.baidu.pbrpc.register.redis.RedisRegistryService;

import redis.clients.jedis.Jedis;

/**
 *
 * @author xiemalin
 * @since 3.2.3
 */
public class RedisCacheableJdkCompiler extends CacheableJdkCompiler implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(RedisRegistryService.class.getName());

    /**
     * 
     */
    private static final String UTF_8 = "utf-8";

    private RedisClient redisClient;

    private String prefix = "Classbytecode_prefix_";
    
    // one week
    private int expireTime = 7 * 24 * 60 * 60;
    
    /**
     * set expireTime value to expireTime
     * @param expireTime the expireTime to set
     */
    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * set redisClient value to redisClient
     * 
     * @param redisClient the redisClient to set
     */
    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * set prefix value to prefix
     * 
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 
     */
    public RedisCacheableJdkCompiler() {
        super(new JdkCompiler(RedisCacheableJdkCompiler.class.getClassLoader()));
    }

    /**
     * @param compiler
     */
    public RedisCacheableJdkCompiler(Compiler compiler) {
        super(compiler);
    }

    /**
     * get the redisClient
     * 
     * @return the redisClient
     */
    public JedisPool getJedisPool() {
        return redisClient.getJedisPool();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.bjf.remoting.protobuf.utils.compiler.Compiler#loadBytes(java.lang.String)
     */
    @Override
    public byte[] loadBytes(String className) {
        return compiler.loadBytes(className);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.bjf.remoting.protobuf.utils.compiler.CacheableJdkCompiler#cached(java.lang.String, long)
     */
    @Override
    protected byte[] cached(String className, long timestamp) {

        String key = prefix + className;
        String field = String.valueOf(timestamp);
        
        LOGGER.info("Try to load bytecode with key '" + key + "' and timestamp '" + field + "'");
        Jedis resource = null;
        try {
            resource = getJedisPool().getResource();
            return resource.hget(key.getBytes(UTF_8), field.getBytes(UTF_8));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e.getCause());

        } finally {
            if (resource != null) {
                getJedisPool().returnResource(resource);
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.bjf.remoting.protobuf.utils.compiler.CacheableJdkCompiler#cache(java.lang.String, byte[], long)
     */
    @Override
    protected void cache(String className, byte[] bytes, long timestamp) {
        
        String key = prefix + className;
        String field = String.valueOf(timestamp);
        LOGGER.info("Try to cache bytecode with key '" + key + "' and timestamp '" + field + "'");
        
        Jedis resource = null;
        try {
            resource = getJedisPool().getResource();
            resource.hsetnx(key.getBytes(UTF_8), field.getBytes(UTF_8), bytes);
            resource.expire(key, expireTime);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e.getCause());
        } finally {
            if (resource != null) {
                getJedisPool().returnResource(resource);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(redisClient, "property 'redisClient' is null.");
        
        try {
            redisClient.init();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e.getCause());
        }

    }

}
