/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.compress;

import java.io.IOException;

/**
 * Compress interface.
 *
 * @author xiemalin
 * @since 1.4
 */
public interface Compress {

    byte[] compress(byte[] array) throws IOException;
    
    
    byte[] unCompress(byte[] array) throws IOException;
}
