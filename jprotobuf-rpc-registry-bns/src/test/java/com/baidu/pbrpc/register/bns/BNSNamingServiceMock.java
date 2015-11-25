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
package com.baidu.pbrpc.register.bns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.baidu.driver4j.bns.Instance;

/**
 * Mock class for {@link BNSNamingService}
 *
 * @author xiemalin
 * @since 3.0.2
 */
public class BNSNamingServiceMock extends BNSNamingService {

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.pbrpc.register.bns.BNSNamingService#doGetInstanceList()
     */
    @Override
    protected List<Instance> doGetInstanceList() {

        List<Instance> ret = new ArrayList<Instance>();

        Instance instance = new Instance();

        instance.setPort("{rpc=1031}");
        try {
            instance.setIp(lookupHost(InetAddress.getLocalHost().getHostName()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ret.add(instance);

        return ret;
    }

    public static int lookupHost(String hostname) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return -1;
        }
        byte[] addrBytes;
        int addr;
        addrBytes = inetAddress.getAddress();
        addr = ((addrBytes[3] & 0xff) << 24) | ((addrBytes[2] & 0xff) << 16) | ((addrBytes[1] & 0xff) << 8)
                | (addrBytes[0] & 0xff);
        return addr;
    }
    
}
