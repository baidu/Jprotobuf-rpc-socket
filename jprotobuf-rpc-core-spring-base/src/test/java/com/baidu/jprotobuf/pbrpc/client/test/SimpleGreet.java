/**
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.jprotobuf.pbrpc.client.test;

/**
 * The Class SimpleGreet.
 *
 * @author xiemalin
 * @since 3.5.22
 */
public class SimpleGreet implements Greet {

    /**
     * Dummy call.
     *
     * @param name the arg
     * @return the string
     */
    @Override
    public String greet(String name) {
        return "Hello " + name;
    }

}
