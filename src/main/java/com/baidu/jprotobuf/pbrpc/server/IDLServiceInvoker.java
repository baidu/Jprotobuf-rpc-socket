/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.server;

import com.baidu.bjf.remoting.protobuf.IDLProxyObject;

/**
 * IDL service invoker
 * 
 * @author xiemalin
 * @since 1.0
 */
public interface IDLServiceInvoker {

    /**
     * RPC service call back method.
     * 
     * @param input
     *            request IDL proxy object by protobuf deserialized
     * @param output
     *            return back IDL proxy object to serialized
     * @throws Exception
     *             in case of any exception
     */
    void invoke(IDLProxyObject input, IDLProxyObject output) throws Exception;
}
