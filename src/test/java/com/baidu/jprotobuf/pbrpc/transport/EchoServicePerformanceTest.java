/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;

/**
 *
 * @author xiemalin
 * @since 1.0
 */
public class EchoServicePerformanceTest extends BasePerformaceTest {
    
    RpcServer rpcServer;
    RpcClient rpcClient;
    EchoInfo echoInfo; 
    EchoService echoService;
    
    RpcDataPackage in;
    RpcDataPackage out;
    
    Runnable runnable = new Runnable() {
        
        public void run() {
            echoService.echo(echoInfo);
            
        }
    };
    
    public void setUp(int threadSize, String requestData, String responseData) {
        rpcServer = new RpcServer();
        EchoServiceImpl echoServiceImpl = new EchoServiceImpl();
        rpcServer.registerService(echoServiceImpl);
        rpcServer.start(PORT);
        
        
        RpcClientOptions options = new RpcClientOptions();
        options.setThreadPoolSize(threadSize);
        options.setMaxIdleSize(threadSize);
        options.setMaxWait(1000);
        
        rpcClient = new RpcClient(options);
        ProtobufRpcProxy<EchoService> pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
        pbrpcProxy.setPort(PORT);
        echoService = pbrpcProxy.proxy();
        
        echoInfo = new EchoInfo();
        echoInfo.setMessage(requestData);
        echoService.echo(echoInfo);
        in = buildPackage(requestData.getBytes(), null, null, "echoService", "echo");
        out = buildPackage(responseData.getBytes(), null, null, "echoService", "echo");
    }
    
    @After
    public void tearDown() {
        rpcClient.stop();
        
        rpcServer.shutdown();
    }

    @Test
    public void performanceOneTreadTest() {
        oneThreadExecute("world", "hello world");
        
    }
    
    @Test
    public void performanceOneTreadTest2() {
        oneThreadExecute("world", "hello world");
        
    }
    
    @Test
    public void performanceOneTreadTestWithLongText() {
        
        String requestString = "";
        String responseString = "";
        for (int i = 0; i < 100; i++) {
            requestString += "world world";
            responseString += "hello world";
        }
        
        oneThreadExecute(requestString, responseString);
        
    }

    /**
     * 
     */
    private void oneThreadExecute(String requestString, String responseString) {
        setUp(1, requestString, responseString);
        int totalRequestSize = 100000;
        
        long time = System.currentTimeMillis();
        for (int i = 0; i < totalRequestSize; i++) {
            echoService.echo(echoInfo);
        }
        long timetook = System.currentTimeMillis() - time;
        
        printResult(in, out, totalRequestSize, timetook, 1);
    }
    
    @Test
    public void performanceTwoTreadsTest() throws Exception {
        int totalRequestSize = 100000;
        int thread = 2;
        long timetook = multiExecute(totalRequestSize, thread, "world", "hello world");
        
        printResult(in, out, totalRequestSize, timetook, thread);
    }
    
    @Test
    public void performanceFourTreadsTest() throws Exception {
        int totalRequestSize = 100000;
        int thread = 4;
        long timetook = multiExecute(totalRequestSize, thread, "world", "hello world");
        
        printResult(in, out, totalRequestSize, timetook, thread);
    }
    
    @Test
    public void performance20TreadsTest() throws Exception {
        int totalRequestSize = 100000;
        int thread = 20;
        long timetook = multiExecute(totalRequestSize, thread, "world", "hello world");
        
        printResult(in, out, totalRequestSize, timetook, thread);
    }
    
    @Test
    public void performance20TreadsTestWithLongText() throws Exception {
        
        String requestString = "";
        String responseString = "";
        for (int i = 0; i < 100; i++) {
            requestString += "world world";
            responseString += "hello world";
        }
        
        int totalRequestSize = 100000;
        int thread = 20;
        long timetook = multiExecute(totalRequestSize, thread, requestString, responseString);
        
        printResult(in, out, totalRequestSize, timetook, thread);
        
    }
    
    @Test
    public void performance40TreadsTestWithLongText() throws Exception {
        
        String requestString = "";
        String responseString = "";
        for (int i = 0; i < 100; i++) {
            requestString += "world world";
            responseString += "hello world";
        }
        
        int totalRequestSize = 100000;
        int thread = 40;
        long timetook = multiExecute(totalRequestSize, thread, requestString, responseString);
        
        printResult(in, out, totalRequestSize, timetook, thread);
        
    }

    /**
     * @param totalRequestSize
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private long multiExecute(int totalRequestSize, int multiSize, String requestData, String responseData) throws InterruptedException, ExecutionException {
        setUp(multiSize, requestData, responseData);
        
        ExecutorService pool = Executors.newFixedThreadPool(2);
        
        long time = System.currentTimeMillis();
        List<Future<?>> futures = new ArrayList<Future<?>>(multiSize);
        for (int i = 0; i < totalRequestSize; i++) {
            Future<?> submit = pool.submit(runnable);
            futures.add(submit);
        }
        for (Future<?> future : futures) {
            future.get();
        }
        
        long timetook = System.currentTimeMillis() - time;
        pool.shutdown();
        return timetook;
    }
}
