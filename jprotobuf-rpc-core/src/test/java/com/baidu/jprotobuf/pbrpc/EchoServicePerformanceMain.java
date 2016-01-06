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

package com.baidu.jprotobuf.pbrpc;

/**
 * 
 * Performance test in main
 *
 * @author xiemalin
 * @since 1.1
 */
public class EchoServicePerformanceMain {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
        System.out.println("-------------------jprotobuf-----------------------------");
        EchoServicePerformanceTest test =  new EchoServicePerformanceTest();
        test.setTotalRequestSize(100000);
        
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
        
        com.baidu.jprotobuf.pbrpc.proto.EchoServicePerformanceTest test2 = new com.baidu.jprotobuf.pbrpc.proto.EchoServicePerformanceTest();
        test2.setTotalRequestSize(100000);
        System.out.println("-------------------protobuf-----------------------------");
        
        test2.performanceOneTreadTest();
        test2.tearDown();
        
        test2.performanceOneTreadTest2();
        test2.tearDown();

        test2.performanceOneTreadTestWithLongText();
        test2.tearDown();
        
        test2.performanceTwoTreadsTest();
        test2.tearDown();
        
        test2.performanceFourTreadsTest();
        test2.tearDown();
        
        test2.performance20TreadsTest();
        test2.tearDown();
        
        test2.performance20TreadsTestWithLongText();
        test2.tearDown();
        
        test2.performance40TreadsTestWithLongText();
        test2.tearDown();
        
        System.exit(0);
    }

}
