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
package com.baidu.jprotobuf.pbrpc.client.ha;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.baidu.jprotobuf.pbrpc.EchoServiceImpl;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;

/**
 * 
 * Base class for ha test
 * 
 * @author xiemalin
 * @since 2.15
 */
public class HaEchoServiceTestBase {
    
    private static final Logger LOG = Logger.getLogger(HaEchoServiceTestBase.class.getName());

    private NamingService namingService;
    
    protected Set<String> defaultServices = new HashSet<String>();
    
    /**
     * get the namingService
     * @return the namingService
     */
    public NamingService getNamingService() {
        return namingService;
    }


    private List<RpcServer> servers;
    
    protected List<InetSocketAddress> list;

    /**
     * 
     */
    public HaEchoServiceTestBase() {

        list = new ArrayList<InetSocketAddress>();

        // server1
        InetSocketAddress address = new InetSocketAddress(1031);
        list.add(address);

        // server2
        address = new InetSocketAddress(1032);
        list.add(address);

        // server3
        address = new InetSocketAddress(1033);
        list.add(address);

        // server4
        address = new InetSocketAddress(1034);
        list.add(address);

        // server4
        address = new InetSocketAddress(1035);
        list.add(address);

        namingService = new DummyNamingService(list);
    }
    
    @Before
    public void setUp() {
        defaultServices.add("");
        try {
            startServers();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        stopServers();
    }
    
    public void startServers() throws Exception {
        
        List<InetSocketAddress> list = namingService.list(defaultServices).get("");
        
        servers = new ArrayList<RpcServer>(list.size());
        int order = 1;
        for (InetSocketAddress inetSocketAddress : list) {
            RpcServer rpcServer = new RpcServer();
            EchoServiceImpl echoServiceImpl = new EchoServiceImpl(order++);
            rpcServer.registerService(echoServiceImpl);
            rpcServer.start(inetSocketAddress);
            servers.add(rpcServer);
        }
        
        RpcServer rpcServer = new RpcServer();
        EchoServiceImpl echoServiceImpl = new EchoServiceImpl(order++);
        rpcServer.registerService(echoServiceImpl);
        rpcServer.start(new InetSocketAddress(1036));
        servers.add(rpcServer);
    }
    
    public void stopOneServer() {
        if (servers != null) {
            for (RpcServer server : servers) {
                try {
                    if (!server.isStop()) {
                        server.shutdown();
                        return;
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
    
    public void recoverServer() {
        stopServers();
        try {
            startServers();
        } catch (Exception e) {
        }
    }
    
    
    public void stopServers() {
        if (servers != null) {
            for (RpcServer server : servers) {
                try {
                    server.shutdown();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

}
