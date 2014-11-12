/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

import java.io.InputStream;

import com.baidu.bjf.remoting.protobuf.IDLProxyObject;

/**
 * Service exporter supports for JProtobuf {@link IDLProxyObject}.<br>
 * This exporter class could be directly publish 
 * 
 * @author xiemalin
 * @since 1.0
 */
public class IDLServiceExporter extends
        AbstractServiceExporter<IDLProxyObject, IDLProxyObject> {
    
    /**
     * input protobuf IDL
     */
    private InputStream inputIDL;

    /**
     * input protobuf IDL defined object name for multiple message object
     * defined select
     */
    private String inputIDLObjectName;

    /**
     * output protobuf IDL
     */
    private InputStream outputIDL;

    /**
     * output protobuf IDL defined object name for multiple message object
     * defined select
     */
    private String outputIDLObjectName;

    private IDLProxyObject inputIDLProxyObject;

    private IDLProxyObject outputIDLProxyObject;
    
    private String inputIDLStr;
    
    private String outputIDLStr;

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.server.ServiceExporter#execute(java.lang.Object)
     */
    public IDLProxyObject execute(IDLProxyObject input) throws Exception {
        
        return null;
    }

}
