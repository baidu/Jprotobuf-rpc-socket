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

import java.io.InputStream;

import com.baidu.bjf.remoting.protobuf.IDLProxyObject;
import com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware;

/**
 * Service exporter supports for JProtobuf {@link IDLProxyObject}.<br>
 * This exporter class could be directly publish 
 * 
 * @author xiemalin
 * @since 1.0
 */
@SuppressWarnings("unused")
public class IDLServiceExporter extends
        AbstractServiceExporter<IDLProxyObject, IDLProxyObject> implements RpcMetaAware {
    
    /** input protobuf IDL. */
    private InputStream inputIDL;

    /** input protobuf IDL defined object name for multiple message object defined select. */
    private String inputIDLObjectName;

    /** output protobuf IDL. */
    private InputStream outputIDL;

    /** output protobuf IDL defined object name for multiple message object defined select. */
    private String outputIDLObjectName;

    /** The input idl proxy object. */
    private IDLProxyObject inputIDLProxyObject;

    /** The output idl proxy object. */
    private IDLProxyObject outputIDLProxyObject;
    
    /** The input idl str. */
    private String inputIDLStr;
    
    /** The output idl str. */
    private String outputIDLStr;

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.server.ServiceExporter#execute(java.lang.Object)
     */
    public IDLProxyObject execute(IDLProxyObject input) throws Exception {
        IDLProxyObject output = getOutputIDLProxyObject();
        getServiceInvoker().invoke(input, output);
        return output;
    }

    /**
     * Gets the input proxy object.
     *
     * @return the input proxy object
     */
    public IDLProxyObject getInputProxyObject() {
        if (inputIDLProxyObject != null) {
            return inputIDLProxyObject.newInstnace();
        }
        
        return inputIDLProxyObject;
    }
    
    /**
     * Gets the output idl proxy object.
     *
     * @return the output idl proxy object
     */
    protected IDLProxyObject getOutputIDLProxyObject() {
        if (outputIDLProxyObject != null) {
            return outputIDLProxyObject.newInstnace();
        }
        return outputIDLProxyObject;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware#getInputMetaProto()
     */
    public String getInputMetaProto() {
        return inputIDLStr;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.meta.RpcMetaAware#getOutputMetaProto()
     */
    public String getOutputMetaProto() {
        return outputIDLStr;
    }
}
