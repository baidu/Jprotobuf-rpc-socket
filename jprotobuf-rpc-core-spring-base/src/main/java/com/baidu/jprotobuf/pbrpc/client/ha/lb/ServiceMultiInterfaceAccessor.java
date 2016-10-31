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

import java.util.ArrayList;
import java.util.List;

/**
 * add extra interface set property.
 * @author xiemalin
 * 
 * @see com.baidu.rigel.platform.util.ServiceInterfaceAccessor
 * @since 1.0.0.0
 */
public abstract class ServiceMultiInterfaceAccessor extends
        ServiceInterfaceAccessor {

    /** The extra interfaces. */
    private List<String> extraInterfaces;

    /**
     * Gets the extra interfaces.
     *
     * @return the extra interfaces
     */
    public List<String> getExtraInterfaces() {
        return extraInterfaces;
    }

    /** The extra service interfaces. */
    private List<Class> extraServiceInterfaces;

    /**
     * Gets the extra service interfaces.
     *
     * @return the extra service interfaces
     */
    public List<Class> getExtraServiceInterfaces() {
        return extraServiceInterfaces;
    }

    /**
     * Sets the extra interfaces.
     *
     * @param extraInterfaces the new extra interfaces
     */
    public void setExtraInterfaces(List<String> extraInterfaces) {
        this.extraInterfaces = extraInterfaces;

        if (extraInterfaces == null || extraInterfaces.isEmpty()) {
            return;
        }
        extraServiceInterfaces = new ArrayList<Class>(extraInterfaces.size());
        Class interfaze;
        try {
            for (String extraInterface : extraInterfaces) {
                interfaze = Class.forName(extraInterface);
                if (!interfaze.isInterface()) {
                    throw new IllegalArgumentException(
                            "'extraServiceInterfaces' must be interface");
                }
                extraServiceInterfaces.add(interfaze);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
