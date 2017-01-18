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

package com.baidu.jprotobuf.pbrpc.client;

import java.io.IOException;
import java.lang.reflect.Method;

import com.baidu.jprotobuf.pbrpc.AuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.DummyAuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.ServerAuthenticationDataHandler;
import com.baidu.jprotobuf.pbrpc.DummyClientAttachmentHandler;
import com.baidu.jprotobuf.pbrpc.DummyLogIDGenerator;
import com.baidu.jprotobuf.pbrpc.LogIDGenerator;
import com.baidu.jprotobuf.pbrpc.ProtobufRPC;
import com.baidu.jprotobuf.pbrpc.utils.ReflectionUtils;
import com.google.protobuf.GeneratedMessage;

/**
 * RPC method description info.
 *
 * @author xiemalin
 * @see ProtobufRpcProxy
 * @see ProtobufRPC
 * @since 1.0
 */
public abstract class RpcMethodInfo {

    /** The method. */
    private Method method;
    
    /** The protobuf prc. */
    private ProtobufRPC protobufPRC;
    
    /** The service name. */
    private String serviceName;
    
    /** The method name. */
    private String methodName;
    
    /** The once talk timeout. */
    private long onceTalkTimeout;

    /** The input class. */
    private Class<? extends Object> inputClass;
    
    /** The output class. */
    private Class<? extends Object> outputClass;

    /** The log id generator. */
    private LogIDGenerator logIDGenerator;
    
    /** The client attachment handler. */
    private ClientAttachmentHandler clientAttachmentHandler;
    /** The authentication data handler. */
    private AuthenticationDataHandler authenticationDataHandler;

    /**
     * Input encode.
     *
     * @param input the input
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract byte[] inputEncode(Object input) throws IOException;

    /**
     * Output decode.
     *
     * @param output the output
     * @return the object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract Object outputDecode(byte[] output) throws IOException;

    /**
     * Checks if is message type.
     *
     * @param method the method
     * @return true, if is message type
     */
    public static boolean isMessageType(Method method) {

        boolean paramMessagetType = false;

        Class<?>[] types = method.getParameterTypes();
        if (types.length == 1) {
            if (GeneratedMessage.class.isAssignableFrom(types[0])) {
                paramMessagetType = true;
            }
        }

        Class<?> returnType = method.getReturnType();
        if (!ReflectionUtils.isVoid(returnType)) {
            if (GeneratedMessage.class.isAssignableFrom(returnType)) {
                if (paramMessagetType) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Invalid RPC method. parameter type and return "
                            + "type should define in same way.");
                }
            }
        }

        return false;
    }

    /**
     * Instantiates a new rpc method info.
     *
     * @param method the method
     * @param protobufPRC the protobuf prc
     */
    public RpcMethodInfo(Method method, ProtobufRPC protobufPRC) {
        this.method = method;
        this.protobufPRC = protobufPRC;

        Class<?>[] types = method.getParameterTypes();
        if (types.length > 1) {
            throw new IllegalArgumentException("RPC method can not has more than one parameter. illegal method:"
                    + method.getName());
        } else if (types.length == 1) {
            inputClass = types[0];
        }

        Class<?> returnType = method.getReturnType();
        if (!ReflectionUtils.isVoid(returnType)) {
            outputClass = returnType;
        }

        // initialize others
        Class<? extends LogIDGenerator> logIDGeneratorClass = protobufPRC.logIDGenerator();
        if (logIDGeneratorClass != DummyLogIDGenerator.class) {
            try {
                logIDGenerator = logIDGeneratorClass.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'logIDGenerator' of class '"
                        + logIDGeneratorClass.getName() + "'");
            }
        }

        Class<? extends ClientAttachmentHandler> attachmentHandlerClass = protobufPRC.attachmentHandler();
        if (attachmentHandlerClass != DummyClientAttachmentHandler.class) {
            try {
                clientAttachmentHandler = attachmentHandlerClass.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'ClientAttachmentHandler' of class '"
                        + attachmentHandlerClass.getName() + "'");
            }
        }
        
        Class<? extends AuthenticationDataHandler> authenticationDataHandlerCls = protobufPRC.authenticationDataHandler();
        if (authenticationDataHandlerCls != DummyAuthenticationDataHandler.class) {
            try {
                authenticationDataHandler = authenticationDataHandlerCls.newInstance();
            } catch (Exception e) {
                throw new IllegalAccessError("Can not initialize 'AuthenticationDataHandler' of class '"
                        + authenticationDataHandlerCls.getName() + "'");
            }
        }
        

    }

    /**
     * Gets the log id generator.
     *
     * @return the log id generator
     */
    public LogIDGenerator getLogIDGenerator() {
        return logIDGenerator;
    }

    /**
     * Gets the client attachment handler.
     *
     * @return the client attachment handler
     */
    public ClientAttachmentHandler getClientAttachmentHandler() {
        return clientAttachmentHandler;
    }

    /**
     * Gets the input class.
     *
     * @return the input class
     */
    public Class<? extends Object> getInputClass() {
        return inputClass;
    }

    /**
     * Gets the output class.
     *
     * @return the output class
     */
    public Class<? extends Object> getOutputClass() {
        return outputClass;
    }

    /**
     * Gets the service name.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the service name.
     *
     * @param serviceName the new service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Gets the method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the method name.
     *
     * @param methodName the new method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the once talk timeout.
     *
     * @return the once talk timeout
     */
    public long getOnceTalkTimeout() {
        return onceTalkTimeout;
    }

    /**
     * Sets the once talk timeout.
     *
     * @param onceTalkTimeout the new once talk timeout
     */
    public void setOnceTalkTimeout(long onceTalkTimeout) {
        this.onceTalkTimeout = onceTalkTimeout;
    }

    /**
     * Gets the method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Gets the protobuf prc.
     *
     * @return the protobuf prc
     */
    public ProtobufRPC getProtobufPRC() {
        return protobufPRC;
    }

    /**
     * get the authenticationDataHandler
     * @return the authenticationDataHandler
     */
    public AuthenticationDataHandler getAuthenticationDataHandler() {
        return authenticationDataHandler;
    }

    
}
