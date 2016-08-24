/*
 * Copyright 2002-2007 the original author or authors.
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
package com.baidu.jprotobuf.pbrpc.client;

import java.net.InetSocketAddress;

/**
 * Simple {@link InetSocketAddress} call back implements {@link ServiceLocatorCallback}.
 *
 * @author xiemalin
 * @since 2.19
 */
public class SimpleServiceLocatorCallabck implements ServiceLocatorCallback {

    /** The address. */
    private InetSocketAddress address;
    
    /** The serivce si string. */
    private String serivceSiString;

    /**
     * Instantiates a new simple service locator callabck.
     *
     * @param address the address
     * @param serivceSiString the serivce si string
     */
    public SimpleServiceLocatorCallabck(InetSocketAddress address, String serivceSiString) {
        super();
        this.address = address;
        this.serivceSiString = serivceSiString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.client.ServiceLocatorCallback#fetchAddress(java.lang.String)
     */
    @Override
    public InetSocketAddress fetchAddress(String serviceSignature) {
        if (this.serivceSiString == null) {
            return null;
        }
        if (this.serivceSiString.equals(serviceSignature)) {
            return address;
        }
        return null;
    }

}
