/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.meta;

import com.baidu.jprotobuf.pbrpc.ProtobufPRC;

/**
 * {@link RpcServiceMetaService}
 *
 * @author xiemalin
 * @since 2.1
 */
public interface RpcServiceMetaService {

    @ProtobufPRC(serviceName = RpcServiceMetaServiceProvider.RPC_META_SERVICENAME)
    RpcServiceMetaList getRpcServiceMetaInfo();
}
