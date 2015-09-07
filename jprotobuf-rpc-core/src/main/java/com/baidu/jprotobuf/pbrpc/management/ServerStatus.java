/*
 * Copyright 2002-2014 the original author or authors.
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
package com.baidu.jprotobuf.pbrpc.management;

import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.BOLD_FONT;
import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.BOLD_FONT_END;
import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.HTML_HEAD;
import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.HTML_TAIL;
import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.LINE_BREAK;
import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.PRE_ENDS;
import static com.baidu.jprotobuf.pbrpc.management.HttpConstants.PRE_STARTS;

import java.util.List;

import com.baidu.jprotobuf.pbrpc.meta.MetaExportHelper;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList;
import com.baidu.jprotobuf.pbrpc.transport.RpcServer;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;

/**
 * Server status
 * 
 *
 * @author xiemalin
 * @since 3.1.0
 */
public class ServerStatus {

    /**
     * 
     */
    private static final int SECONDS_IN_HOUR = 3600;
    /**
     * 
     */
    private static final int SECONDS_IN_DAY = 86400;
    private int port;
    private int httpPort;
    private long startTime;

    private RpcServer rpcServer;

    public ServerStatus(RpcServer rpcServer) {
        super();
        this.rpcServer = rpcServer;
        startTime = rpcServer.getStartTime();

        port = rpcServer.getInetSocketAddress().getPort();
        httpPort = rpcServer.getRpcServerOptions().getHttpServerPort();

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(HTML_HEAD);
        ret.append("Server online: ").append(getOnlineDuration(startTime)).append(LINE_BREAK);
        ret.append("RPC port:").append(port).append(LINE_BREAK);
        ret.append("Http management port:").append(httpPort).append(LINE_BREAK);
        ret.append("Chunk enabled").append(LINE_BREAK);
        ret.append("Compress enabled(Gzip Snappy)").append(LINE_BREAK);
        ret.append("Attachment enabled").append(LINE_BREAK);

        ret.append(LINE_BREAK).append(LINE_BREAK);
        ret.append(PRE_STARTS);
        ret.append("--------------properties info(").append(RpcServerOptions.class.getDeclaredFields().length)
                .append(")----------------").append(LINE_BREAK);
        ret.append(rpcServer.getRpcServerOptions());

        ret.append(LINE_BREAK).append(LINE_BREAK);
        
        RpcServiceMetaList exportRPCMeta = MetaExportHelper.exportRPCMeta(port);

        List<RpcServiceMeta> metaList = exportRPCMeta.getRpcServiceMetas();
        if (metaList != null) {
            ret.append("--------------RPC service list(").append(metaList.size()).append(") ----------------")
                    .append(LINE_BREAK);

            for (RpcServiceMeta rpcServiceMeta : metaList) {
                ret.append(BOLD_FONT).append("Service name:").append(rpcServiceMeta.getServiceName())
                        .append(LINE_BREAK);
                ret.append("Method name:").append(rpcServiceMeta.getMethodName()).append(BOLD_FONT_END)
                        .append(LINE_BREAK);

                ret.append("Request IDL:").append(LINE_BREAK).append(rpcServiceMeta.getInputProto()).append(LINE_BREAK);
                ret.append("Response IDL:").append(LINE_BREAK).append(rpcServiceMeta.getOutputProto())
                        .append(LINE_BREAK);

                ret.append(LINE_BREAK);
            }

        }

        ret.append(LINE_BREAK).append(LINE_BREAK);

        ret.append("--------------protobuf idl info ----------------").append(LINE_BREAK);
        ret.append(exportRPCMeta.getTypesIDL());
        ret.append(exportRPCMeta.getRpcsIDL());
        ret.append(PRE_ENDS);
        ret.append(HTML_TAIL);

        return ret.toString();
    }

    private String getOnlineDuration(long startTime) {
        StringBuilder ret = new StringBuilder();
        long ms = (System.currentTimeMillis() - startTime) / 1000;

        long days = ms / SECONDS_IN_DAY;
        long hours = (ms % SECONDS_IN_DAY) / SECONDS_IN_HOUR;
        long seconds = ((ms % SECONDS_IN_DAY) % SECONDS_IN_HOUR);

        ret.append(days).append(" days ").append(hours).append(" hours ").append(seconds).append(" seconds");

        return ret.toString();
    }
}
