/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

/**
 * Abstract service exporter class.
 *
 * @author xiemalin
 * @since 1.0
 */
public abstract class AbstractServiceExporter<I, O> implements ServiceExporter<I, O> {

    private IDLServiceInvoker serviceInvoker;
    
    /**
     * get the serviceInvoker
     * @return the serviceInvoker
     */
    public IDLServiceInvoker getServiceInvoker() {
        return serviceInvoker;
    }

    /**
     * set serviceInvoker value to serviceInvoker
     * @param serviceInvoker the serviceInvoker to set
     */
    public void setServiceInvoker(IDLServiceInvoker serviceInvoker) {
        this.serviceInvoker = serviceInvoker;
    }


    /**
     * service name
     */
    private String serviceName;
    
    /**
     * method name
     */
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
     * set serviceName value to serviceName
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * set methodName value to methodName
     * @param methodName the methodName to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


}
