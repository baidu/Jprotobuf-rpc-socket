/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.data;

import java.nio.charset.Charset;

/**
 * Protocol constant definition.
 *
 * @author xiemalin
 * @since 1.0
 */
public class ProtocolConstant {

    /**
     * default magic code
     */
    public static final String MAGIC_CODE = "PRPC";
    
    /**
     * default charset
     */
    public static Charset CHARSET = Charset.forName("utf-8");

    /**
     * get the cHARSET
     * @return the cHARSET
     */
    public static Charset getCharSet() {
        return CHARSET;
    }

    /**
     * set charSet value to CHARSET
     * @param charSet the CHARSET to set
     */
    public static void setCharset(Charset charSet) {
        CHARSET = charSet;
    }
    
    
    
}
