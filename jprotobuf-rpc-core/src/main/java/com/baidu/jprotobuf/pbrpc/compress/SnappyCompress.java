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
package com.baidu.jprotobuf.pbrpc.compress;

import java.io.IOException;

import org.xerial.snappy.Snappy;

/**
 * Compress support by Snappy by snappy-java client.
 * more information pls visit: https://github.com/xerial/snappy-java
 *
 * @author xiemalin
 * @since 2.12
 */
public class SnappyCompress implements Compress {

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.compress.Compress#compress(byte[])
     */
    public byte[] compress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }
        return Snappy.compress(array);
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.compress.Compress#unCompress(byte[])
     */
    public byte[] unCompress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }
        return Snappy.uncompress(array);
    }

}
