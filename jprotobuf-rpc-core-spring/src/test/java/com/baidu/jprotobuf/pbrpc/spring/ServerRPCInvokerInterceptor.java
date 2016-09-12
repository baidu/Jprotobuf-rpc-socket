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
package com.baidu.jprotobuf.pbrpc.spring;

import org.springframework.util.Assert;

import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.intercept.MethodInvocationInfo;

/**
 * A  {@link InvokerInterceptor} implements to print intercepter message for server
 *
 * @author xiemalin
 * @since 3.4.0
 */
public class ServerRPCInvokerInterceptor implements InvokerInterceptor {
	
	private String from;
	
	private boolean failed = false;
	
	/**
	 * set failed value to failed
	 * @param failed the failed to set
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	/**
	 * set from value to from
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/* (non-Javadoc)
	 * @see com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor#beforeInvoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void beforeInvoke(MethodInvocationInfo methodInvocation) {
		if (failed) {
			throw new RuntimeException("This is a exception. " + from);
		}
		
		System.out.println("ServerRPCInvokerInterceptor--> method=" + methodInvocation.getMethod().getName() + " this is from " + from);

	}

	/* (non-Javadoc)
	 * @see com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor#process(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object process(MethodInvocationInfo methodInvocation) {
		Assert.notNull(methodInvocation.getMethod());
		return null;
	}

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor#afterProcess()
     */
    @Override
    public void afterProcess() {
        
    }

}
