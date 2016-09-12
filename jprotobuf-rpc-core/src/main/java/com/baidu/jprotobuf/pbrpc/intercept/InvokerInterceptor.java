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

/**
 * RPC method invoker intercepter.
 *
 * @author xiemalin
 * @since 3.4.0
 */
public interface InvokerInterceptor {
	
	/**
	 * This method will call before RPC method invoke.
	 *
	 * @param methodInvocation the method invocation
	 */
	void beforeInvoke(MethodInvocationInfo methodInvocation);

	/**
	 * to do intercept action.
	 *
	 * @param methodInvocation the method invocation
	 * @return if not null, this intercepter will active and this result will replace to real RPC return.<br>
	 *         if return null will continue to another intercepter.
	 */
	Object process(MethodInvocationInfo methodInvocation);
	
	
	/**
	 * to call after process called. here need to clear some garbage here
	 */
	void afterProcess();
}
