package com.baidu.jprotobuf.pbrpc.meta;

import com.google.protobuf.*;
import java.io.IOException;
import com.baidu.bjf.remoting.protobuf.utils.*;
import java.lang.reflect.*;
import com.baidu.bjf.remoting.protobuf.*;
import java.util.*;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta;

public class RpcServiceMeta$$JProtoBufClass implements
    com.baidu.bjf.remoting.protobuf.Codec<com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta> {
    public byte[] encode(RpcServiceMeta t) throws IOException {
        int size = 0;
        com.google.protobuf.ByteString f_1 = null;
        if (!CodedConstant.isNull(t.getServiceName())) {
            f_1 = com.google.protobuf.ByteString.copyFromUtf8(t.getServiceName());
        }
        if (!CodedConstant.isNull(t.getServiceName())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(1, f_1);
        }
        com.google.protobuf.ByteString f_2 = null;
        if (!CodedConstant.isNull(t.getMethodName())) {
            f_2 = com.google.protobuf.ByteString.copyFromUtf8(t.getMethodName());
        }
        if (!CodedConstant.isNull(t.getMethodName())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(2, f_2);
        }
        com.google.protobuf.ByteString f_3 = null;
        if (!CodedConstant.isNull(t.getInputProto())) {
            f_3 = com.google.protobuf.ByteString.copyFromUtf8(t.getInputProto());
        }
        if (!CodedConstant.isNull(t.getInputProto())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(3, f_3);
        }
        com.google.protobuf.ByteString f_4 = null;
        if (!CodedConstant.isNull(t.getOutputProto())) {
            f_4 = com.google.protobuf.ByteString.copyFromUtf8(t.getOutputProto());
        }
        if (!CodedConstant.isNull(t.getOutputProto())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(4, f_4);
        }
        System.out.println("encode:" + size);
        final byte[] result = new byte[size];
        final CodedOutputStream output = CodedOutputStream.newInstance(result);
        if (f_1 != null) {
            output.writeBytes(1, f_1);
        }
        if (f_2 != null) {
            output.writeBytes(2, f_2);
        }
        if (f_3 != null) {
            output.writeBytes(3, f_3);
        }
        if (f_4 != null) {
            output.writeBytes(4, f_4);
        }
        return result;
    }

    public RpcServiceMeta decode(byte[] bb) throws IOException {
        com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta ret = new com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta();
        CodedInputStream input = CodedInputStream.newInstance(bb, 0, bb.length);
        try {
            boolean done = false;
            Codec codec = null;
            while (!done) {
                int tag = input.readTag();
                if (tag == 0) {
                    break;
                }
                if (tag == CodedConstant.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setServiceName(input.readString());
                    continue;
                }
                if (tag == CodedConstant.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setMethodName(input.readString());
                    continue;
                }
                if (tag == CodedConstant.makeTag(3, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setInputProto(input.readString());
                    continue;
                }
                if (tag == CodedConstant.makeTag(4, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setOutputProto(input.readString());
                    continue;
                }
                input.skipField(tag);
            }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        }
        return ret;
    }

    public int size(RpcServiceMeta t) throws IOException {
        int size = 0;
        com.google.protobuf.ByteString f_1 = null;
        if (!CodedConstant.isNull(t.getServiceName())) {
            f_1 = com.google.protobuf.ByteString.copyFromUtf8(t.getServiceName());
        }
        if (!CodedConstant.isNull(t.getServiceName())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(1, f_1);
        }
        com.google.protobuf.ByteString f_2 = null;
        if (!CodedConstant.isNull(t.getMethodName())) {
            f_2 = com.google.protobuf.ByteString.copyFromUtf8(t.getMethodName());
        }
        if (!CodedConstant.isNull(t.getMethodName())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(2, f_2);
        }
        com.google.protobuf.ByteString f_3 = null;
        if (!CodedConstant.isNull(t.getInputProto())) {
            f_3 = com.google.protobuf.ByteString.copyFromUtf8(t.getInputProto());
        }
        if (!CodedConstant.isNull(t.getInputProto())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(3, f_3);
        }
        com.google.protobuf.ByteString f_4 = null;
        if (!CodedConstant.isNull(t.getOutputProto())) {
            f_4 = com.google.protobuf.ByteString.copyFromUtf8(t.getOutputProto());
        }
        if (!CodedConstant.isNull(t.getOutputProto())) {
            size += com.google.protobuf.CodedOutputStream.computeBytesSize(4, f_4);
        }
        System.out.println(size);
        return size;
    }

    public void writeTo(RpcServiceMeta t, CodedOutputStream output) throws IOException {
        int size = 0;
        com.google.protobuf.ByteString f_1 = null;
        if (!CodedConstant.isNull(t.getServiceName())) {
            f_1 = com.google.protobuf.ByteString.copyFromUtf8(t.getServiceName());
        }
        com.google.protobuf.ByteString f_2 = null;
        if (!CodedConstant.isNull(t.getMethodName())) {
            f_2 = com.google.protobuf.ByteString.copyFromUtf8(t.getMethodName());
        }
        com.google.protobuf.ByteString f_3 = null;
        if (!CodedConstant.isNull(t.getInputProto())) {
            f_3 = com.google.protobuf.ByteString.copyFromUtf8(t.getInputProto());
        }
        com.google.protobuf.ByteString f_4 = null;
        if (!CodedConstant.isNull(t.getOutputProto())) {
            f_4 = com.google.protobuf.ByteString.copyFromUtf8(t.getOutputProto());
        }
        if (f_1 != null) {
            output.writeBytes(1, f_1);
        }
        if (f_2 != null) {
            output.writeBytes(2, f_2);
        }
        if (f_3 != null) {
            output.writeBytes(3, f_3);
        }
        if (f_4 != null) {
            output.writeBytes(4, f_4);
        }
    }

    public RpcServiceMeta readFrom(CodedInputStream input) throws IOException {
        com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta ret = new com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta();
        try {
            boolean done = false;
            Codec codec = null;
            while (!done) {
                int tag = input.readTag();
                if (tag == 0) {
                    break;
                }
                if (tag == CodedConstant.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setServiceName(input.readString());
                    continue;
                }
                if (tag == CodedConstant.makeTag(2, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setMethodName(input.readString());
                    continue;
                }
                if (tag == CodedConstant.makeTag(3, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setInputProto(input.readString());
                    continue;
                }
                if (tag == CodedConstant.makeTag(4, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    ret.setOutputProto(input.readString());
                    continue;
                }
                input.skipField(tag);
            }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        }
        return ret;
    }
}