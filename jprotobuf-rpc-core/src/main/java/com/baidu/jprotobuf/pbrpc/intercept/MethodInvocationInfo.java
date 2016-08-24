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

import java.lang.reflect.Method;

/**
 * A method invocation includes all needed information for method invoke process.
 *
 * @author xiemalin
 * @since 3.4.1
 */
public class MethodInvocationInfo {

	/** The target. */
	private Object target;
	
	/** The args. */
	private Object[] args;
	
	/** The method. */
	private Method method;
	
	/** The extra params. */
	private byte[] extraParams;
	

	/**
	 * Instantiates a new method invocation info.
	 *
	 * @param target the target
	 * @param args the args
	 * @param method the method
	 * @param extraParams the extra params
	 */
	public MethodInvocationInfo(Object target, Object[] args, Method method, byte[] extraParams) {
		super();
		this.target = target;
		this.args = args;
		this.method = method;
		this.extraParams = extraParams;
	}

	/**
	 * Gets the args.
	 *
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * Sets the args.
	 *
	 * @param args the new args
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Sets the method.
	 *
	 * @param method the new method
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Gets the extra params.
	 *
	 * @return the extra params
	 */
	public byte[] getExtraParams() {
		return extraParams;
	}

	/**
	 * Sets the extra params.
	 *
	 * @param extraParams the new extra params
	 */
	public void setExtraParams(byte[] extraParams) {
		this.extraParams = extraParams;
	}
	
	
	
	
	
}
