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

package com.baidu.jprotobuf.pbrpc.server;

/**
 * Abstract service exporter class.
 *
 * @author xiemalin
 * @param <I> the generic type
 * @param <O> the generic type
 * @since 1.0
 */
public abstract class AbstractServiceExporter<I, O> implements ServiceExporter<I, O> {

    /** The service invoker. */
    private IDLServiceInvoker serviceInvoker;
    
    /**
     * Gets the service invoker.
     *
     * @return the service invoker
     */
    public IDLServiceInvoker getServiceInvoker() {
        return serviceInvoker;
    }

    /**
     * Sets the service invoker.
     *
     * @param serviceInvoker the new service invoker
     */
    public void setServiceInvoker(IDLServiceInvoker serviceInvoker) {
        this.serviceInvoker = serviceInvoker;
    }


    /** service name. */
    private String serviceName;
    
    /** method name. */
    private String methodName;
    
    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.server.ServiceExporter#getServiceName()
     */
    public String getServiceName() {
        return serviceName;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.server.ServiceExporter#getMethodName()
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the service name.
     *
     * @param serviceName the new service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Sets the method name.
     *
     * @param methodName the new method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


}
