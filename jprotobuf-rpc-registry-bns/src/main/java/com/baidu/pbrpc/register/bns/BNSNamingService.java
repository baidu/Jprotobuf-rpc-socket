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

import com.baidu.driver4j.bns.BNSQueryAgentProxy;
import com.baidu.driver4j.bns.Instance;
import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * Registry service support by BNS.
 *
 * @author xiemalin
 * @since 3.0.2
 */
public class BNSNamingService implements NamingService, InitializingBean {

    /**
     * log this class
     */
    protected static final Log LOGGER = LogFactory.getLog(BNSNamingService.class.getName());

    /**
     * this BNSClient constructor method is doing nothing.
     */
    private BNSQueryAgentProxy proxy = BNSQueryAgentProxy.proxy();

    private int timeout = 3000;

    private String bnsName;

    private String portName = "rpc";

    /**
     * set portName value to portName
     * 
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
     * 
     * @param multiPortsSplit the multiPortsSplit to set
     */
    @Deprecated
    public void setMultiPortsSplit(String multiPortsSplit) {
        // no longer use will remove later
    }

    /**
     * set portSplit value to portSplit
     * 
     * @param portSplit the portSplit to set
     */
    @Deprecated
    public void setPortSplit(String portSplit) {
        // no longer use will remove later
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
        List<Instance> instanceList = doGetInstanceList();

        List<RegisterInfo> registerInfos = wrapRegisterInfos(instanceList);

        for (String serviceSignature : serviceSignatures) {
            ret.put(serviceSignature, registerInfos);
        }

        return ret;
    }

    protected List<Instance> doGetInstanceList() {
        // get bns name
        List<Instance> instanceList = proxy.getInstanceByService(bnsName, timeout);
        return instanceList;
    }

    /**
     * @param instanceList
     * @return
     */
    protected List<RegisterInfo> wrapRegisterInfos(List<Instance> instanceList) {
        if (instanceList == null || instanceList.isEmpty()) {
            return null;
        }

        List<RegisterInfo> ret = new ArrayList<RegisterInfo>();
        for (Instance bnsInstance : instanceList) {
            String ip = bnsInstance.getDottedIP();

            RegisterInfo registerInfo = new RegisterInfo();
            registerInfo.setHost(ip);
            registerInfo.setPort(buildRpcPort(portName, bnsInstance.getPorts(), bnsInstance.getPort()));
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
    private int buildRpcPort(String portName, Map<String, String> ports, int defaultProt) {
        if (ports == null || ports.isEmpty()) {
            return defaultProt;
        }

        String sPort = ports.get(portName);
        if (sPort == null) {
            return defaultProt;
        }

        return StringUtils.toInt(sPort);

    }

}
