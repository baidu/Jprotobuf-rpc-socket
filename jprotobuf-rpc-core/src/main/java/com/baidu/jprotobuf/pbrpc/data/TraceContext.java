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

/**
 * 分布式追踪上下文.
 *
 * @see <a href="https://www.w3.org/TR/trace-context/">W3C - Trace Context</a>
 * @see <a href="https://github.com/opentracing/specification/blob/master/rfc/trace_identifiers.md">Open Tracing - Trace
 *      Identifiers</a>
 */
public class TraceContext {

    /** The Trace ID. */
    private Long traceId;

    /** The Span ID. */
    private Long spanId;

    /** The Parent Span ID (optional). */
    private Long parentSpanId;

    /**
     * Instantiates a new trace context.
     *
     * @param traceId the trace id
     * @param spanId the span id
     * @param parentSpanId the parent span id
     */
    public TraceContext(Long traceId, Long spanId, Long parentSpanId) {
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
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
}
