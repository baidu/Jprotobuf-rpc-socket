/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compress supports by GZIP
 * 
 * @author xiemalin
 * @since 1.4
 */
public class GZipCompress implements Compress {

    /**
     * default buffer size
     */
    private static final int BUFFER_SIZE = 256;

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.compress.Compress#compress(byte[])
     */
    public byte[] compress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(array);
            gzip.close();
        } catch (IOException e) {
            throw e;
        }
        return out.toByteArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.compress.Compress#unCompress(byte[])
     */
    public byte[] unCompress(byte[] array) throws IOException {
        if (array == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(array);

        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
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
