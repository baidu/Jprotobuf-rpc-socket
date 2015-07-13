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

import com.baidu.jprotobuf.pbrpc.data.ProtocolConstant;
import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;

/**
 * Base performace test case.
 * 
 * @author xiemalin
 * @since 1.0
 */
public abstract class BasePerformaceTest extends BaseTest {

    String formatString = "|%20s|%20s|%22s|%20s|%20s|";

    String formatString2 = "|%10d|%10d|%12d|%10d|%10d|";

    protected void printResult(RpcDataPackage in, RpcDataPackage out, int totalCount, long totaltime, int threadCount) {
        System.out.println("---------------------Performance Result-------------------------");
        System.out.println("send byte size: " + in.write().length + ";receive byte size: " + out.write().length);
        System.out
                .println(String.format(formatString, "total count", "time took(ms)", "average(ms)", "QPS", "threads"));
        double avg = (totaltime * 1.0 / totalCount);
        double qps = totalCount * 1000.0 / totaltime;
        System.out.println(String.format(formatString, totalCount, totaltime, new Double(avg).longValue(), new Double(qps).longValue(), threadCount));
        System.out.println("---------------------Performance Result-------------------------");
    }

    protected RpcDataPackage buildPackage(byte[] data, byte[] attachment, byte[] authenticationData,
        String serivceName, String methodName) {
        RpcDataPackage dataPackage = new RpcDataPackage();
        dataPackage.magicCode(ProtocolConstant.MAGIC_CODE).data(data).attachment(attachment)
                .authenticationData(authenticationData);
        dataPackage.serviceName(serivceName).methodName(methodName);
        return dataPackage;

    }
}
