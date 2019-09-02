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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class TraceTest.
 * 
 * @author xiemalin
 * @since 4.0.0
 */
public class TraceTest {
    
    /** The trace id. */
    Long traceId = 100L;
    
    /** The trace key. */
    String traceKey = "dsfsdfa";
    
    /** The zero. */
    Long zero = 0L;
    
    /** The trace. */
    Trace trace;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        trace = new Trace(traceId, traceKey, 0L, 0L);
    }

    /**
     * Test property set.
     */
    @Test
    public void testPropertyGetterSetter() {
        assertTrace(traceId, traceKey, zero, zero, trace);
        Assert.assertNull(trace.getParent());
    }
    
    /**
     * Assert trace.
     *
     * @param traceId the trace id
     * @param traceKey the trace key
     * @param spanId the span id
     * @param parentId the parent id
     */
    private void assertTrace(Long traceId, String traceKey, Long spanId, Long parentId, Trace trace) {
        Assert.assertEquals(traceId, trace.getTraceId());
        Assert.assertEquals(traceKey, trace.getTraceKey());
        Assert.assertEquals(zero, trace.getSpanId());
        Assert.assertEquals(zero, trace.getParentSpanId());
    }

    /**
     * Test step info.
     */
    @Test
    public void testStepInfo() {
        trace.stepInto();
        
        assertTrace(traceId, traceKey, zero, zero, trace.getParent());
        
        assertTrace(traceId, traceKey, zero, zero, trace);
        
    }
    
    /**
     * Test step over.
     */
    @Test
    public void testStepOver() {
        trace.stepOver();
        
        Assert.assertNull(trace.getParent());
        
        assertTrace(traceId + 1, traceKey, zero, zero, trace);
        
    }
    
    /**
     * Test step return.
     */
    @Test
    public void testStepReturn() {
        trace.stepInto();
        trace.stepOver();
        trace.stepReturn();
        
        Assert.assertNull(trace.getParent());
        
        assertTrace(traceId, traceKey, zero, zero, trace);
        
    }
}
