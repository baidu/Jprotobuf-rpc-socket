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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;
import com.baidu.noah.naming.BNSClient;
import com.baidu.noah.naming.BNSInstance;

/**
 * Registry service support by BNS.
 *
 * @author xiemalin
 * @since 3.0.2
 */
public class BNSNamingServiceOld implements NamingService, InitializingBean {
    
    /**
     * log this class
     */
    protected static final Log LOGGER = LogFactory.getLog(BNSNamingServiceOld.class.getName());

    /**
     *  port split string
     */
    private String portSplit = "=";

    /**
     * multiple ports split string
     */
    private String multiPortsSplit = ",";

    /**
     * this BNSClient constructor method is doing nothing.
     */
    private BNSClient bnsClient = new BNSClient();

    private int timeout = 3000;

    private String bnsName;
    
    private String portName = "rpc";
    
    /**
     * set portName value to portName
     * @param portName the portName to set
     */
    public void setPortName(String portName) {
        this.portName = portName;
    }

    /**
     * set bnsName value to bnsName
     * 
     * @param bnsName the bnsName to set
     */
    public void setBnsName(String bnsName) {
        this.bnsName = bnsName;
    }

    /**
     * set timeout value to timeout
     * 
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /**
     * set multiPortsSplit value to multiPortsSplit
     * @param multiPortsSplit the multiPortsSplit to set
     */
    public void setMultiPortsSplit(String multiPortsSplit) {
        this.multiPortsSplit = multiPortsSplit;
    }
    
    /**
     * set portSplit value to portSplit
     * @param portSplit the portSplit to set
     */
    public void setPortSplit(String portSplit) {
        this.portSplit = portSplit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(bnsName, "property 'bnsName' is null.");
        Assert.notNull(portName, "property 'portName' is null.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingService#list(java.util.Set)
     */
    @Override
    public Map<String, List<RegisterInfo>> list(Set<String> serviceSignatures) throws Exception {

        Map<String, List<RegisterInfo>> ret = new HashMap<String, List<RegisterInfo>>();
        if (serviceSignatures == null) {
            return ret;
        }

        // get bns name
        List<BNSInstance> instanceList = doGetInstanceList();

        List<RegisterInfo> registerInfos = wrapRegisterInfos(instanceList);

        for (String serviceSignature : serviceSignatures) {
            ret.put(serviceSignature, registerInfos);
        }

        return ret;
    }
    
    
    protected List<BNSInstance> doGetInstanceList() {
        // get bns name
        List<BNSInstance> instanceList = bnsClient.getInstanceByService(bnsName, timeout);
        return instanceList;
    }

    /**
     * @param instanceList
     * @return
     */
    protected List<RegisterInfo> wrapRegisterInfos(List<BNSInstance> instanceList) {
        if (instanceList == null || instanceList.isEmpty()) {
            return null;
        }

        List<RegisterInfo> ret = new ArrayList<RegisterInfo>();
        for (BNSInstance bnsInstance : instanceList) {
            String ip = bnsInstance.getDottedIP();

            RegisterInfo registerInfo = new RegisterInfo();
            registerInfo.setHost(ip);
            registerInfo.setPort(buildRpcPort(portName, bnsInstance.getMultiPort(), bnsInstance.getPort()));
            ret.add(registerInfo);
        }

        return ret;
    }

    /**
     * get port value by port name, if port name not found, default port will return
     * 
     * @param portName name of port
     * @param multiPort port string of name and port
     * @param defaultProt default port if port name not found this port will return
     * @return the port by port name
     */
    private int buildRpcPort(String portName, String multiPort, int defaultProt) {
        if (!StringUtils.isEmpty(portName) && !StringUtils.isEmpty(multiPort)) {
            String[] multiPorts = multiPort.split(multiPortsSplit);
            for (String mp : multiPorts) {
                String[] ports = mp.split(portSplit);
                if (ports.length == 2 && !StringUtils.isEmpty(ports[0])) {
                    if (ports[0].equals(portName)) {
                        return StringUtils.toInt(ports[1], defaultProt); 
                    }
                } else {
                    LOGGER.error("Detect invalid port setting, value is'" + mp + "'");
                }
            }
        }
        
        return defaultProt;
    }

}
