/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.spring;

/**
 * Placehoader resolved call back 
 * 
 * @author xiemalin
 * @since 2.17
 */
public interface PlaceholderResolved {

    /**
     * parsed placeholder value returned.
     * @param placeholder
     * @return parsed string
     */
    String doResolved(String placeholder);
}
