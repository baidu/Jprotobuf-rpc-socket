Jprotobuf-rpc-socket
====================

Protobuf RPC是一种基于TCP协议的二进制RPC通信协议。它以Protobuf作为基本的数据交换格式，并基于Protobuf内置的RPC Service形式，规定了通信双方之间的数据交换协议，以实现完整的RPC调用。

## 协议规范 ##
[https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/RPC%E9%80%9A%E8%AE%AF%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83](https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/RPC%E9%80%9A%E8%AE%AF%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83 "协议规范")


## 使用示例 ##



## 性能测试 ##

机器配置：
- Linux 64G内存 6核 12线程 
- Intel(R) Xeon(R) CPU           E5645  @ 2.40GHz

性能测试结果如下：
```property
---------------------Performance Result-------------------------
send byte size: 40;receive byte size: 46
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                7535|                     0|               14285|                   1|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 1135;receive byte size: 1135
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|               10055|                     0|               10000|                   1|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 40;receive byte size: 46
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                5022|                     0|               20000|                   2|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 40;receive byte size: 46
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                4969|                     0|               25000|                   4|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 40;receive byte size: 46
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                4927|                     0|               25000|                  20|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 1135;receive byte size: 1135
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                5861|                     0|               20000|                  20|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 1135;receive byte size: 1135
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                5814|                     0|               20000|                  40|

```