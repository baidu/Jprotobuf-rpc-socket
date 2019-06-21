/*
 * Copyright 2002-2014 the original author or authors.
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

package com.baidu.jprotobuf.pbrpc.server;

import java.io.IOException;
import java.lang.reflect.Method;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.jprotobuf.pbrpc.ProtobufRPCService;

/**
 * RPC handler for Jprotobuf annotation.
 * 
 * @author xiemalin
 * @since 1.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AnnotationRpcHandler extends AbstractAnnotationRpcHandler {

    /** The input codec. */
    private Codec inputCodec;

    /** The output codec. */
    private Codec outputCodec;


    /**
     * Instantiates a new annotation rpc handler.
     *
     * @param method the method
     * @param service the service
     * @param protobufPRCService the protobuf prc service
     */
    public AnnotationRpcHandler(Method method, Object service, ProtobufRPCService protobufPRCService) {
        super(method, service, protobufPRCService);
        if (getInputClass() != null) {
            if (!byte[].class.equals(getInputClass())) {
                inputCodec = ProtobufProxy.create(getInputClass());
            }
            
        }
        if (getOutputClass() != null) {
            outputCodec = ProtobufProxy.create(getOutputClass());
        }

    }

    /**
     * Encode input param.
     *
     * @param bytes the bytes
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected Object encodeInputParam(byte[] bytes) throws Exception {
        if (inputCodec != null && bytes != null) {
            Object input = inputCodec.decode(bytes);
            return input;
        }

        return null;
    }
    
    /**
     * Decode output param.
     *
     * @param output the output
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected byte[] decodeOutputParam(Object output) throws Exception {
        if (outputCodec != null) {
            byte[] b = outputCodec.encode(output);
            return b;
        }

        return null;
    }
    

   

}
