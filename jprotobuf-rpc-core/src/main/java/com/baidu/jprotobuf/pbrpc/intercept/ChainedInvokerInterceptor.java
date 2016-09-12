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
package com.baidu.jprotobuf.pbrpc.intercept;

import java.util.List;

/**
 * A chained {@link InvokerInterceptor} for multiple intercepter to apply.
 *
 * @author xiemalin
 * @since 3.4.1
 */
public class ChainedInvokerInterceptor implements InvokerInterceptor {
	
	/** The interceptors. */
	private List<InvokerInterceptor> interceptors;
	
	/**
	 * Sets the interceptors.
	 *
	 * @param interceptors the new interceptors
	 */
	public void setInterceptors(List<InvokerInterceptor> interceptors) {
		this.interceptors = interceptors;
	}

	/* (non-Javadoc)
	 * @see com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor#beforeInvoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public void beforeInvoke(MethodInvocationInfo methodInvocationInfo) {
		if (interceptors == null) {
			return;
		}

		for (InvokerInterceptor interceptor : interceptors) {
			interceptor.beforeInvoke(methodInvocationInfo);
		}
	}

	/* (non-Javadoc)
	 * @see com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor#process(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object process(MethodInvocationInfo methodInvocationInfo) {
		if (interceptors == null) {
			return null;
		}
		
		Object result = null;
		for (InvokerInterceptor interceptor : interceptors) {
			Object ret = interceptor.process(methodInvocationInfo);
			if (ret != null) {
				result = ret;
			}
		}

		return result;
	}

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor#afterProcess()
     */
    @Override
    public void afterProcess() {
        if (interceptors == null) {
            return;
        }

        for (InvokerInterceptor interceptor : interceptors) {
            interceptor.afterProcess();
        }
    }

}
