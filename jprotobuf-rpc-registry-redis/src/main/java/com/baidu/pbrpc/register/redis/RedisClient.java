package com.baidu.pbrpc.register.redis;

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Protocol;


/**
 * redis客户端
 * 
 * @author xiemalin
 * @since 1.0
 */
public class RedisClient {
    
    /**
     * cache name
     */
    private String cacheName = "default";
    /**
     * redis server ip
     */
    private String redisServer;
    /**
     * redis server authenticate key
     */
    private String redisAuthKey;
    /**
     * {@link JedisPool} instance
     */
    private JedisPool jedisPool;

    /**
     * get the jedisPool
     * 
     * @return the jedisPool
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * set jedisPool value to jedisPool
     * 
     * @param jedisPool
     *            the jedisPool to set
     */
    protected void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * redis server port
     */
    private int port = Protocol.DEFAULT_PORT;
    /**
     * operation time out
     */
    private int timeout = Protocol.DEFAULT_TIMEOUT;

    /**
     * if maxIdle == 0, ObjectPool has 0 size pool
     */
    private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;

    /**
     * max wait time
     */
    private long maxWait = GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;

    /**
     * set if support test client workable on get client
     */
    private boolean testOnBorrow = GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW;

    /**
     * set min idle count
     */
    private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;

    /**
     * set max idle count
     */
    private int maxActive = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;

    /**
     * set if support test client workable on return client to pool
     */
    private boolean testOnReturn = GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN;

    /**
     * set if support test client workable while idle
     */
    private boolean testWhileIdle = GenericObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE;

    /**
     * set time between eviction runs during in Milliseconds
     */
    private long timeBetweenEvictionRunsMillis = GenericObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    /**
     * set number tests per eviction to run
     */
    private int numTestsPerEvictionRun = GenericObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    /**
     * set min evictable idle count in milliseconds
     */
    private long minEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * softMinEvictableIdleTimeMillis
     */
    private long softMinEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * lifo
     */
    private boolean lifo = GenericObjectPoolConfig.DEFAULT_LIFO;

    /**
     * init the JedisPoolObjectFactory and ObjectPool
     */
    @PostConstruct
    public void init() {

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // maxIdle为负数时，sop中不对pool size大小做限制，此处做限制，防止保持过多空闲redis连接
        if (this.maxIdle >= 0) {
            poolConfig.setMaxIdle(this.maxIdle);
        }
        poolConfig.setMaxWaitMillis(this.maxWait);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setTestOnReturn(testOnReturn);
        poolConfig.setTestWhileIdle(testWhileIdle);
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        poolConfig.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        poolConfig.setLifo(lifo);
        jedisPool = new JedisPool(poolConfig, redisServer, port, timeout,
                redisAuthKey);
    }

    /**
     * set time out value
     * 
     * @param timeout
     *            time out value
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void close() {
        this.jedisPool.destroy();

    }

    /**
     * get the redisServer
     * 
     * @return the redisServer
     */
    public String getRedisServer() {
        return redisServer;
    }

    /**
     * set the redisServer
     * 
     * @param redisServer
     *            the redisServer
     */
    public void setRedisServer(String redisServer) {
        this.redisServer = redisServer;
    }

    /**
     * get the cacheName
     * 
     * @return the cacheName
     */
    public String getCacheName() {
        return cacheName;
    }

    /**
     * set cacheName value to cacheName
     * 
     * @param cacheName
     *            the cacheName to set
     */
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * get the redisAuthKey
     * 
     * @return the redisAuthKey
     */
    public String getRedisAuthKey() {
        return redisAuthKey;
    }

    /**
     * set redisAuthKey value to redisAuthKey
     * 
     * @param redisAuthKey
     *            the redisAuthKey to set
     */
    public void setRedisAuthKey(String redisAuthKey) {
        this.redisAuthKey = redisAuthKey;
    }

    /**
     * get the port
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * set port value to port
     * 
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * get the maxIdle
     * 
     * @return the maxIdle
     */
    public int getMaxIdle() {
        return maxIdle;
    }

    /**
     * set maxIdle value to maxIdle
     * 
     * @param maxIdle
     *            the maxIdle to set
     */
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    /**
     * get the maxWait
     * 
     * @return the maxWait
     */
    public long getMaxWait() {
        return maxWait;
    }

    /**
     * set maxWait value to maxWait
     * 
     * @param maxWait
     *            the maxWait to set
     */
    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    /**
     * get the testOnBorrow
     * 
     * @return the testOnBorrow
     */
    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    /**
     * set testOnBorrow value to testOnBorrow
     * 
     * @param testOnBorrow
     *            the testOnBorrow to set
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    /**
     * get the minIdle
     * 
     * @return the minIdle
     */
    public int getMinIdle() {
        return minIdle;
    }

    /**
     * set minIdle value to minIdle
     * 
     * @param minIdle
     *            the minIdle to set
     */
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    /**
     * get the maxActive
     * 
     * @return the maxActive
     */
    public int getMaxActive() {
        return maxActive;
    }

    /**
     * set maxActive value to maxActive
     * 
     * @param maxActive
     *            the maxActive to set
     */
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    /**
     * get the testOnReturn
     * 
     * @return the testOnReturn
     */
    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    /**
     * set testOnReturn value to testOnReturn
     * 
     * @param testOnReturn
     *            the testOnReturn to set
     */
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    /**
     * get the testWhileIdle
     * 
     * @return the testWhileIdle
     */
    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    /**
     * set testWhileIdle value to testWhileIdle
     * 
     * @param testWhileIdle
     *            the testWhileIdle to set
     */
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    /**
     * get the timeBetweenEvictionRunsMillis
     * 
     * @return the timeBetweenEvictionRunsMillis
     */
    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    /**
     * set timeBetweenEvictionRunsMillis value to timeBetweenEvictionRunsMillis
     * 
     * @param timeBetweenEvictionRunsMillis
     *            the timeBetweenEvictionRunsMillis to set
     */
    public void setTimeBetweenEvictionRunsMillis(
        long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    /**
     * get the numTestsPerEvictionRun
     * 
     * @return the numTestsPerEvictionRun
     */
    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    /**
     * set numTestsPerEvictionRun value to numTestsPerEvictionRun
     * 
     * @param numTestsPerEvictionRun
     *            the numTestsPerEvictionRun to set
     */
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    /**
     * get the minEvictableIdleTimeMillis
     * 
     * @return the minEvictableIdleTimeMillis
     */
    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    /**
     * set minEvictableIdleTimeMillis value to minEvictableIdleTimeMillis
     * 
     * @param minEvictableIdleTimeMillis
     *            the minEvictableIdleTimeMillis to set
     */
    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    /**
     * get the softMinEvictableIdleTimeMillis
     * 
     * @return the softMinEvictableIdleTimeMillis
     */
    public long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    /**
     * set softMinEvictableIdleTimeMillis value to
     * softMinEvictableIdleTimeMillis
     * 
     * @param softMinEvictableIdleTimeMillis
     *            the softMinEvictableIdleTimeMillis to set
     */
    public void setSoftMinEvictableIdleTimeMillis(
        long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    /**
     * get the lifo
     * 
     * @return the lifo
     */
    public boolean isLifo() {
        return lifo;
    }

    /**
     * set lifo value to lifo
     * 
     * @param lifo
     *            the lifo to set
     */
    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    /**
     * get the timeout
     * 
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

}
