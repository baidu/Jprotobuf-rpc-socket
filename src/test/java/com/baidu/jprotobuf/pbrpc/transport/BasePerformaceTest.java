/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.transport;

import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;

/**
 * Base test case.
 *
 * @author xiemalin
 * @since 1.0
 */
public abstract class BasePerformaceTest {
    protected static final String HOST = System.getProperty("host", "127.0.0.1");
    protected static final int PORT = Integer.parseInt(System.getProperty("port", "1031"));
    
    String formatString = "|%20s|%20s|%22s|%20s|%20s|";
    
    String formatString2 = "|%10d|%10d|%12d|%10d|%10d|";
    
    protected void printResult(RpcDataPackage in, RpcDataPackage out, int totalCount, long totaltime, int threadCount) {
        System.out.println("---------------------Performance Result-------------------------");
        System.out.println("send byte size: " + in.write().length + ";receive byte size: " + out.write().length);
        System.out.println(String.format(formatString, "total count", "time took(ms)", "average(ms)", "QPS", "threads"));
        long avg = (totaltime / totalCount);
        long qps = totalCount / ( (totaltime / 1000) == 0 ? 1 : (totaltime / 1000));
        System.out.println(String.format(formatString, totalCount, totaltime, avg, qps, threadCount));
        System.out.println("---------------------Performance Result-------------------------");
    }
    
    protected RpcDataPackage buildPackage(byte[] data, 
        byte[] attachment, byte[] authenticationData,  String serivceName, String methodName) {
        RpcDataPackage dataPackage = new RpcDataPackage();
        dataPackage.magicCode(ProtocolConstant.MAGIC_CODE).data(data).attachment(attachment).authenticationData(authenticationData);
        dataPackage.serviceName(serivceName).methodName(methodName);
        return dataPackage;
        
        
    }
}
