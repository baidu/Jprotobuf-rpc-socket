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

package com.baidu.jprotobuf.pbrpc.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;
import com.baidu.jprotobuf.pbrpc.RpcHandler;
import com.baidu.jprotobuf.pbrpc.server.RpcServiceRegistry;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * {@link RpcServiceMetaServiceProvider} service.
 *
 * @author xiemalin
 * @since 2.1
 */
public class RpcServiceMetaServiceProvider {

    /** The Constant LINE_BREAK. */
    private static final String LINE_BREAK = "\n";

    /** The Constant RPC_META_SERVICENAME. */
    public static final String RPC_META_SERVICENAME = "__rpc_meta_watch_service__";

    /** The rpc service registry. */
    private RpcServiceRegistry rpcServiceRegistry;

    /** The rpc service meta list. */
    private RpcServiceMetaList rpcServiceMetaList;

    /**
     * Instantiates a new rpc service meta service provider.
     *
     * @param rpcServiceRegistry the rpc service registry
     */
    public RpcServiceMetaServiceProvider(RpcServiceRegistry rpcServiceRegistry) {
        super();
        this.rpcServiceRegistry = rpcServiceRegistry;
    }

    /**
     * Gets the rpc service meta info.
     *
     * @return the rpc service meta info
     */
    @ProtobufRPCService(serviceName = RPC_META_SERVICENAME)
    public RpcServiceMetaList getRpcServiceMetaInfo() {

        // just cache once
        if (rpcServiceMetaList != null) {
            return rpcServiceMetaList;
        }

        StringBuilder typesIDL = new StringBuilder();
        StringBuilder rpcsIDL = new StringBuilder();

        Map<String, StringBuilder> rpcIDLMap = new HashMap<String, StringBuilder>();

        final Set<Class<?>> cachedTypes = new HashSet<Class<?>>();
        final Set<Class<?>> cachedEnumTypes = new HashSet<Class<?>>();

        Collection<RpcHandler> services = rpcServiceRegistry.getServices();
        List<RpcServiceMeta> list = new ArrayList<RpcServiceMeta>(services.size());
        for (RpcHandler rpcHandler : services) {
            if (rpcHandler instanceof RpcMetaAware) {
                RpcMetaAware meta = (RpcMetaAware) rpcHandler;
                String serviceName = rpcHandler.getServiceName();
                if (RPC_META_SERVICENAME.equals(serviceName)) {
                    continue;
                }
                RpcServiceMeta rpcServiceMeta = new RpcServiceMeta();
                rpcServiceMeta.setServiceName(serviceName);
                rpcServiceMeta.setMethodName(rpcHandler.getMethodName());
                if (rpcHandler.getInputClass() != null) {
                    rpcServiceMeta.setInputObjName(rpcHandler.getInputClass().getSimpleName());

                    String idl =
                            ProtobufIDLGenerator.getIDL(rpcHandler.getInputClass(), cachedTypes, cachedEnumTypes, true);
                    if (idl != null) {
                        typesIDL.append(meta.getInputMetaProto()).append(LINE_BREAK);
                    }
                }
                rpcServiceMeta.setInputProto(meta.getInputMetaProto());
                if (rpcHandler.getOutputClass() != null) {
                    rpcServiceMeta.setOutputObjName(rpcHandler.getOutputClass().getSimpleName());

                    String idl =
                            ProtobufIDLGenerator
                                    .getIDL(rpcHandler.getOutputClass(), cachedTypes, cachedEnumTypes, true);
                    if (idl != null) {
                        typesIDL.append(meta.getOutputMetaProto()).append(LINE_BREAK);
                    }
                }
                rpcServiceMeta.setOutputProto(meta.getOutputMetaProto());
                list.add(rpcServiceMeta);

                StringBuilder rpc = rpcIDLMap.get(serviceName);
                if (rpc == null) {
                    rpc = new StringBuilder();
                    rpcIDLMap.put(serviceName, rpc);
                }
                rpc.append("rpc ").append(rpcHandler.getMethodName()).append("(");
                if (rpcHandler.getInputClass() != null) {
                    rpc.append(rpcHandler.getInputClass().getSimpleName()).append(") ");
                }
                if (rpcHandler.getOutputClass() != null) {
                    rpc.append("returns (").append(rpcHandler.getOutputClass().getSimpleName()).append(");");
                }
                if (!StringUtils.isBlank(rpcHandler.getDescription())) {
                    rpc.append(" //").append(rpcHandler.getDescription());
                }
                
                rpc.append(LINE_BREAK);
            }
        }

        Iterator<Entry<String, StringBuilder>> iter = rpcIDLMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, StringBuilder> entry = iter.next();
            rpcsIDL.append("service ").append(entry.getKey()).append(" {").append(LINE_BREAK);
            rpcsIDL.append(entry.getValue());
            rpcsIDL.append("}").append(LINE_BREAK);
        }

        rpcServiceMetaList = new RpcServiceMetaList();
        rpcServiceMetaList.setRpcServiceMetas(list);
        rpcServiceMetaList.setTypesIDL(typesIDL.toString());
        rpcServiceMetaList.setRpcsIDL(rpcsIDL.toString());
        return rpcServiceMetaList;
    }

    /**
     * Ping.
     */
    @ProtobufRPCService(serviceName = RPC_META_SERVICENAME)
    public void ping() {
        // here just to test service is available
    }
}
