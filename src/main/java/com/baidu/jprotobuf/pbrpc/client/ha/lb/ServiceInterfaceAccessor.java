package com.baidu.jprotobuf.pbrpc.client.ha.lb;

/**
 * Service Interface Accessor
 * @author xiemalin
 * @since 1.0.0.0
 */
public abstract class ServiceInterfaceAccessor {

	private Class serviceInterface;
	
	/**
	 * Set the interface of the service to access.
	 * The interface must be suitable for the particular service and remoting strategy.
	 * <p>Typically required to be able to create a suitable service proxy,
	 * but can also be optional if the lookup returns a typed proxy.
	 */
	public void setServiceInterface(Class serviceInterface) {
		if (serviceInterface != null && !serviceInterface.isInterface()) {
			throw new IllegalArgumentException("'serviceInterface' must be an interface");
		}
		this.serviceInterface = serviceInterface;
	}

	/**
	 * Return the interface of the service to access.
	 */
	public Class getServiceInterface() {
		return this.serviceInterface;
	}
}
