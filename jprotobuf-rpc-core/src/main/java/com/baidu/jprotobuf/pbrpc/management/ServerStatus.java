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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

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
    
    private transient boolean close = false;

    public static final ConcurrentHashMap<String, AtomicLong> REQUEST_COUNTS =
            new ConcurrentHashMap<String, AtomicLong>();
    
    public static final BlockingQueue<RequestInfo> ASYNC_REQUEST = new LinkedBlockingQueue<ServerStatus.RequestInfo>();
    
    private static ExecutorService es;

    public static void incr(String serviceSignature, long timetook) {
        
        ASYNC_REQUEST.add(new RequestInfo(timetook, serviceSignature));
    }
    
    private static void doIncr(RequestInfo requestInfo) {
        AtomicLong newOne = new AtomicLong();
        AtomicLong oldOne = REQUEST_COUNTS.putIfAbsent(requestInfo.signature, newOne);
        if (oldOne != null) {
            newOne = oldOne;
        }
        newOne.incrementAndGet();
    }

    public ServerStatus(RpcServer rpcServer) {
        super();
        this.rpcServer = rpcServer;
        startTime = rpcServer.getStartTime();

        port = rpcServer.getInetSocketAddress().getPort();
        httpPort = rpcServer.getRpcServerOptions().getHttpServerPort();

        es = Executors.newSingleThreadExecutor();
        
        es.execute(new Runnable() {
            
            @Override
            public void run() {
                while (!close) {
                    
                    try {
                        RequestInfo requestInfo = ASYNC_REQUEST.take();
                        doIncr(requestInfo);
                    } catch (InterruptedException e) {
                    }
                }
                
            }
        });
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

        ret.append("--------------Request Info ----------------").append(LINE_BREAK);
        Map<String, AtomicLong> copy = new HashMap<String, AtomicLong>(REQUEST_COUNTS);
        Iterator<Entry<String, AtomicLong>> iterator = copy.entrySet().iterator();
        ret.append("<table><tr><td>service</td><td>request count</td></tr>");
        while (iterator.hasNext()) {
            Entry<String, AtomicLong> next = iterator.next();
            ret.append("<tr>");
            ret.append("<td>").append(next.getKey()).append("</td>");
            ret.append("<td>").append(next.getValue()).append("</td>");
            ret.append("</tr>");
        }
        ret.append("</table>");
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
    
    private static class RequestInfo {
        private long took;
        private String signature;
        public RequestInfo(long took, String signature) {
            super();
            this.took = took;
            this.signature = signature;
        }
        
        
    }

    /**
     * 
     */
    public void close() {
        close = true;
        
        if (es != null) {
            es.shutdown();
        }
        
    }
}
