/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Baidu company (the "License");
 * you may not use this file except in compliance with the License.
 *
 */
package com.baidu.jprotobuf.pbrpc.proto;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.baidu.jprotobuf.pbrpc.proto.EchoInfoClass.EchoInfo;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;

/**
 *
 * @author xiemalin
 *
 */
public class ProtoPrint {

    /**
     * @param args
     */
    public static void main(String[] args) {

        DescriptorProto proto = EchoInfo.getDescriptor().toProto();
        
        String serviceName = proto.getName();
        System.out.println(serviceName);
        
        Map<FieldDescriptor, Object> allFields = proto.getAllFields();
        Iterator<Entry<FieldDescriptor, Object>> iterator = allFields.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<FieldDescriptor, Object> entry = iterator.next();
            
            String name = entry.getKey().getName();
            System.out.println(name);
            int number = entry.getKey().getNumber();
            System.out.println(number);
            Type type = entry.getKey().getType();
            System.out.println(type);
            
            
        }
        
        String string = proto.toString();
        System.out.println(string);

    }

}
