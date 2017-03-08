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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compress supports by GZIP.
 *
 * @author xiemalin
 * @since 1.4
 */
public class GZipCompress implements Compress {

    /** default buffer size. */
    private static final int BUFFER_SIZE = 256;

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.compress.Compress#compress(byte[])
     */
    public byte[] compress(byte[] array) throws IOException {
        return compress0(array);
    }
    
    public byte[] compress0(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }

        byte[] ret = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(array);
            gzip.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            throw e;
        }
        return ret;
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.compress.Compress#unCompress(byte[])
     */
    public byte[] unCompress(byte[] array) throws IOException {
        return unCompress0(array);
    }
    
    public byte[] unCompress0(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(array);

        try {
            GZIPInputStream gunzip = new GZIPInputStream(in) {
                
            };
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw e;
        }
        return out.toByteArray();
    }

}
