package com.baidu.jprotobuf.pbrpc.meta;

import com.google.protobuf.*;
import java.io.IOException;
import com.baidu.bjf.remoting.protobuf.utils.*;
import java.lang.reflect.*;
import com.baidu.bjf.remoting.protobuf.*;
import java.util.*;
import com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList;

public class RpcServiceMetaList$$JProtoBufClass implements
    com.baidu.bjf.remoting.protobuf.Codec<com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList> {
    public byte[] encode(RpcServiceMetaList t) throws IOException {
        int size = 0;
        List f_1 = null;
        if (!CodedConstant.isNull(t.getRpcServiceMetas())) {
            f_1 = t.getRpcServiceMetas();
        }
        if (!CodedConstant.isNull(t.getRpcServiceMetas())) {
            size += CodedConstant.computeListSize(1, f_1, FieldType.OBJECT);
        }
        final byte[] result = new byte[size];
        final CodedOutputStream output = CodedOutputStream.newInstance(result);
        if (f_1 != null) {
            CodedConstant.writeToList(output, 1, FieldType.OBJECT, f_1);
        }
        return result;
    }

    public RpcServiceMetaList decode(byte[] bb) throws IOException {
        com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList ret = new com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList();
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
                    codec = ProtobufProxy.create(com.baidu.jprotobuf.pbrpc.meta.RpcServiceMeta.class);
                    int length = input.readRawVarint32();
                    final int oldLimit = input.pushLimit(length);
                    if ((ret.getRpcServiceMetas()) == null) {
                        List __list = new ArrayList();
                        ret.setRpcServiceMetas(__list);
                    }
                    (ret.getRpcServiceMetas()).add((RpcServiceMeta) codec.readFrom(input));
                    input.checkLastTagWas(0);
                    input.popLimit(oldLimit);
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

    public int size(RpcServiceMetaList t) throws IOException {
        int size = 0;
        List f_1 = null;
        if (!CodedConstant.isNull(t.getRpcServiceMetas())) {
            f_1 = t.getRpcServiceMetas();
        }
        if (!CodedConstant.isNull(t.getRpcServiceMetas())) {
            size += CodedConstant.computeListSize(1, f_1, FieldType.OBJECT);
        }
        return size;
    }

    public void writeTo(RpcServiceMetaList t, CodedOutputStream output) throws IOException {
        List f_1 = null;
        if (!CodedConstant.isNull(t.getRpcServiceMetas())) {
            f_1 = t.getRpcServiceMetas();
        }
        if (f_1 != null) {
            CodedConstant.writeToList(output, 1, FieldType.OBJECT, f_1);
        }
    }

    public RpcServiceMetaList readFrom(CodedInputStream input) throws IOException {
        com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList ret = new com.baidu.jprotobuf.pbrpc.meta.RpcServiceMetaList();
        try {
            boolean done = false;
            Codec codec = null;
            while (!done) {
                int tag = input.readTag();
                if (tag == 0) {
                    break;
                }
                if (tag == CodedConstant.makeTag(1, WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
                    codec = ProtobufProxy.create(RpcServiceMeta.class);
                    int length = input.readRawVarint32();
                    final int oldLimit = input.pushLimit(length);
                    if ((ret.getRpcServiceMetas()) == null) {
                        List __list = new ArrayList();
                        ret.setRpcServiceMetas(__list);
                    }
                    (ret.getRpcServiceMetas()).add((RpcServiceMeta) codec.readFrom(input));
                    input.checkLastTagWas(0);
                    input.popLimit(oldLimit);
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