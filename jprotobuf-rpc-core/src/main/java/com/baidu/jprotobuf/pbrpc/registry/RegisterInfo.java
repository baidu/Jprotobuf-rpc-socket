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
package com.baidu.jprotobuf.pbrpc.registry;

import java.util.Map;

/**
 * POJO class of reigster info.
 * 
 * @author xiemalin
 * @since 2.27
 */
public class RegisterInfo {

    /** host info. */
    private String host;

    /** The port. */
    private int port;

    /** the unique mark for each service info. */
    private String service;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((service == null) ? 0 : service.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RegisterInfo other = (RegisterInfo) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (protocol == null) {
            if (other.protocol != null) {
                return false;
            }
        } else if (!protocol.equals(other.protocol)) {
            return false;
        }
        if (service == null) {
            if (other.service != null) {
                return false;
            }
        } else if (!service.equals(other.service)) {
            return false;
        }
        return true;
    }

    /** protocol info. */
    private String protocol;

    /** The extra infos. */
    private Map<String, String> extraInfos;

    /**
     * Gets the host info.
     *
     * @return the host info
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host info.
     *
     * @param host the new host info
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the unique mark for each service info.
     *
     * @return the unique mark for each service info
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the unique mark for each service info.
     *
     * @param service the new unique mark for each service info
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Gets the protocol info.
     *
     * @return the protocol info
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol info.
     *
     * @param protocol the new protocol info
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the extra infos.
     *
     * @return the extra infos
     */
    public Map<String, String> getExtraInfos() {
        return extraInfos;
    }

    /**
     * Sets the extra infos.
     *
     * @param extraInfos the extra infos
     */
    public void setExtraInfos(Map<String, String> extraInfos) {
        this.extraInfos = extraInfos;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
        this.port = port;
    }

}
