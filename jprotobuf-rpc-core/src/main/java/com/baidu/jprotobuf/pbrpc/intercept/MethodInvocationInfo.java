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
package com.baidu.jprotobuf.pbrpc.intercept;

import java.lang.reflect.Method;

/**
 * A method invocation includes all needed information for method invoke process
 * 
 *
 * @author xiemalin
 * @since 3.4.1
 */
public class MethodInvocationInfo {

	private Object target;
	private Object[] args;
	private Method method;
	
	private byte[] extraParams;
	

	/**
	 * @param target
	 * @param args
	 * @param method
	 * @param extraParams
	 */
	public MethodInvocationInfo(Object target, Object[] args, Method method, byte[] extraParams) {
		super();
		this.target = target;
		this.args = args;
		this.method = method;
		this.extraParams = extraParams;
	}

	/**
	 * get the args
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * set args value to args
	 * @param args the args to set
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

	/**
	 * get the target
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * set target value to target
	 * @param target the target to set
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

	/**
	 * get the method
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * set method value to method
	 * @param method the method to set
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * get the extraParams
	 * @return the extraParams
	 */
	public byte[] getExtraParams() {
		return extraParams;
	}

	/**
	 * set extraParams value to extraParams
	 * @param extraParams the extraParams to set
	 */
	public void setExtraParams(byte[] extraParams) {
		this.extraParams = extraParams;
	}
	
	
	
	
	
}
