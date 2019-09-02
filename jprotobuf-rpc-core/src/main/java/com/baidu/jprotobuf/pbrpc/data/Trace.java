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
package com.baidu.jprotobuf.pbrpc.data;

import java.util.UUID;

/**
 * 分布式追踪上下文.
 *
 * @author xiemalin
 * @since 4.0.0
 * @see <a href="https://www.w3.org/TR/trace-context/">W3C - Trace Context</a>
 * @see <a href="https://github.com/opentracing/specification/blob/master/rfc/trace_identifiers.md">Open Tracing - Trace
 *      Identifiers</a>
 */
public class Trace {

    /** The Trace ID. */
    private Long traceId;
    
    /** The trace key. */
    private String traceKey;

    /** The Span ID. */
    private Long spanId;

    /** The Parent Span ID (optional). */
    private Long parentSpanId;
    
    /** The parent. */
    private Trace parent;
    

    /**
     * Instantiates a new trace context.
     *
     * @param traceId the trace id
     * @param traceKey the trace key
     * @param spanId the span id
     * @param parentSpanId the parent span id
     */
    public Trace(Long traceId, String traceKey, Long spanId, Long parentSpanId) {
        this.traceId = traceId;
        this.traceKey = traceKey;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
    }
    
    /**
     * New parent trace with current time(MS) as the value for trace id .
     *
     * @return the trace
     */
    public static Trace newParentTrace() {
        return new Trace(System.currentTimeMillis(), uuid(), 0L, 0L);
    }
    
    /**
     * Step into.
     *
     * @return the trace
     */
    public void stepInto() {
        Trace newParent = copy();
        parentSpanId = spanId;
        spanId = 0L;
        parent = newParent;
    }
    
    /**
     * Step over.
     */
    public void stepOver() {
        spanId++;
    }
    
    /**
     * Step return.
     *
     * @param trace the trace
     */
    public void stepReturn() {
        if (parent == null) {
            return;
        }
        spanId = parent.getSpanId();
        parentSpanId = parent.getParentSpanId();
        parent = parent.parent;
    }
    
    /**
     * Copy.
     *
     * @return the trace
     */
    public Trace copy() {
        Trace trace = new Trace(traceId, traceKey, spanId, parentSpanId);
        trace.parent = parent;
        return trace;
    }
    
    /**
     * return unique string by random UUID string.
     *
     * @return the string
     */
    private static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Gets the trace id.
     *
     * @return the trace id
     */
    public Long getTraceId() {
        return traceId;
    }

    /**
     * Sets the trace id.
     *
     * @param traceId the new trace id
     */
    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    /**
     * Gets the span id.
     *
     * @return the span id
     */
    public Long getSpanId() {
        return spanId;
    }

    /**
     * Sets the span id.
     *
     * @param spanId the new span id
     */
    public void setSpanId(Long spanId) {
        this.spanId = spanId;
    }

    /**
     * Gets the parent span id.
     *
     * @return the parent span id
     */
    public Long getParentSpanId() {
        return parentSpanId;
    }

    /**
     * Sets the parent span id.
     *
     * @param parentSpanId the new parent span id
     */
    public void setParentSpanId(Long parentSpanId) {
        this.parentSpanId = parentSpanId;
    }
    
    /**
     * Gets the trace key.
     *
     * @return the trace key
     */
    public String getTraceKey() {
        return traceKey;
    }
    
    /**
     * Sets the trace key.
     *
     * @param traceKey the new trace key
     */
    public void setTraceKey(String traceKey) {
        this.traceKey = traceKey;
    }
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Trace getParent() {
        return parent;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Trace [traceId=" + traceId + ", traceKey=" + traceKey + ", spanId=" + spanId + ", parentSpanId="
                + parentSpanId + "]";
    }
}
