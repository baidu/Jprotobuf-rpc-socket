/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

/**
 *
 * @author xiemalin
 *
 */
public class EchoServicePerformanceMain {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        EchoServicePerformanceTest test =  new EchoServicePerformanceTest();
        
        test.performanceOneTreadTest();
        test.tearDown();
        
        test.performanceOneTreadTest2();
        test.tearDown();

        test.performanceOneTreadTestWithLongText();
        test.tearDown();
        
        test.performanceTwoTreadsTest();
        test.tearDown();
        
        test.performanceFourTreadsTest();
        test.tearDown();
        
        test.performance20TreadsTest();
        test.tearDown();
        
        test.performance20TreadsTestWithLongText();
        test.tearDown();
        
        test.performance40TreadsTestWithLongText();
        test.tearDown();
        
        System.exit(0);
    }

}
