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
package com.baidu.jprotobuf.pbrpc.client.ha.lb;

/**
 * Service Interface Accessor.
 *
 * @author xiemalin
 * @since 1.0.0.0
 */
public abstract class ServiceInterfaceAccessor {

	/** The service interface. */
	private Class serviceInterface;
	
	/**
	 * Sets the service interface.
	 *
	 * @param serviceInterface the new service interface
	 */
	public void setServiceInterface(Class serviceInterface) {
		if (serviceInterface != null && !serviceInterface.isInterface()) {
			throw new IllegalArgumentException("'serviceInterface' must be an interface");
		}
		this.serviceInterface = serviceInterface;
	}

	/**
	 * Gets the service interface.
	 *
	 * @return the service interface
	 */
	public Class getServiceInterface() {
		return this.serviceInterface;
	}
}
