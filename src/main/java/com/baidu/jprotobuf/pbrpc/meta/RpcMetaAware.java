/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.meta;

/**
 * To get Rpc service method description
 *
 * @author xiemalin
 * @since 2.1
 */
public interface RpcMetaAware {

    /**
     * get method parameter proto description
     * @return null if has no parameter
     */
    String getInputMetaProto();
    
    /**
     * get method return type proto description
     * @return null if return type is void
     */
    String getOutputMetaProto();
    
}
