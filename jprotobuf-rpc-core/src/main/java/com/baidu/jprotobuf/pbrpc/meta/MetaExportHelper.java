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

import java.util.List;

import com.baidu.jprotobuf.pbrpc.client.ProtobufRpcProxy;
import com.baidu.jprotobuf.pbrpc.transport.RpcClient;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * Export meta info helper class.
 *
 * @author xiemalin
 * @since 3.1.0
 */
public class MetaExportHelper {

    /** default output charset name. */
    public static final String CHARSET_NAME = "utf-8";

    /**
     * Export idl.
     *
     * @param servicePort the service port
     * @return the string
     */
    public static String exportIDL(int servicePort) {
        return exportIDL(null, servicePort, CHARSET_NAME);
    }

    /**
     * Export idl.
     *
     * @param serviceHost the service host
     * @param servicePort the service port
     * @return the string
     */
    public static String exportIDL(String serviceHost, int servicePort) {
        return exportIDL(serviceHost, servicePort, CHARSET_NAME);
    }

    /**
     * Export idl.
     *
     * @param serviceHost the service host
     * @param servicePort the service port
     * @param charset the charset
     * @return the string
     */
    public static String exportIDL(String serviceHost, int servicePort, String charset) {
        StringBuilder ret = new StringBuilder();
        RpcClient rpcClient = new RpcClient();

        ProtobufRpcProxy<RpcServiceMetaService> pbrpcProxy =
                new ProtobufRpcProxy<RpcServiceMetaService>(rpcClient, RpcServiceMetaService.class);
        pbrpcProxy.setPort(servicePort);
        if (serviceHost != null) {
            pbrpcProxy.setHost(serviceHost);
        }
        RpcServiceMetaService proxy = pbrpcProxy.proxy();

        RpcServiceMetaList rpcServiceMetaInfo = proxy.getRpcServiceMetaInfo();

        try {
            String typesIDL = rpcServiceMetaInfo.getTypesIDL();
            if (!StringUtils.isBlank(typesIDL)) {
                ret.append(typesIDL);
            }
            String rpcsIDL = rpcServiceMetaInfo.getRpcsIDL();
            if (!StringUtils.isBlank(rpcsIDL)) {
                ret.append(rpcsIDL);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            // here need do close here to free resource
            pbrpcProxy.close();
            rpcClient.stop();
        }
        return ret.toString();

    }
    
    /**
     * Export rpc meta.
     *
     * @param servicePort the service port
     * @return the rpc service meta list
     */
    public static RpcServiceMetaList exportRPCMeta(int servicePort) {
        return exportRPCMeta(null, servicePort);
    }

    /**
     * Export rpc meta.
     *
     * @param serviceHost the service host
     * @param servicePort the service port
     * @return the rpc service meta list
     */
    public static RpcServiceMetaList exportRPCMeta(String serviceHost, int servicePort) {
        RpcClient rpcClient = new RpcClient();

        ProtobufRpcProxy<RpcServiceMetaService> pbrpcProxy =
                new ProtobufRpcProxy<RpcServiceMetaService>(rpcClient, RpcServiceMetaService.class);
        pbrpcProxy.setPort(servicePort);
        if (serviceHost != null) {
            pbrpcProxy.setHost(serviceHost);
        }

        try {
            RpcServiceMetaService proxy = pbrpcProxy.proxy();

            RpcServiceMetaList rpcServiceMetaInfo = proxy.getRpcServiceMetaInfo();

            return rpcServiceMetaInfo;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            // here need do close here to free resource
            pbrpcProxy.close();
            rpcClient.stop();
        }

    }
}
