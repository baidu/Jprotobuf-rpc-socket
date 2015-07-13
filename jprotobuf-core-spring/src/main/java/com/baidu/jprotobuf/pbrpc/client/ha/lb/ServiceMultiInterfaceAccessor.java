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

    private List<String> extraInterfaces;

    /**
     * @return the extraInterfaces
     */
    public List<String> getExtraInterfaces() {
        return extraInterfaces;
    }

    private List<Class> extraServiceInterfaces;

    /**
     * @return the extraServiceInterfaces
     */
    public List<Class> getExtraServiceInterfaces() {
        return extraServiceInterfaces;
    }

    /**
     * @param extraInterfaces
     *            the extraInterfaces to set
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
