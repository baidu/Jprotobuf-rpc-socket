Jprotobuf-rpc-socket
====================

Protobuf RPC是一种基于TCP协议的二进制RPC通信协议。它以Protobuf作为基本的数据交换格式，并基于Protobuf内置的RPC Service形式，规定了通信双方之间的数据交换协议，以实现完整的RPC调用。

关联项目：
[https://github.com/jhunters/jprotobuf](https://github.com/jhunters/jprotobuf "https://github.com/jhunters/jprotobuf")

## 协议规范 ##
[https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/RPC%E9%80%9A%E8%AE%AF%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83](https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/RPC%E9%80%9A%E8%AE%AF%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83](https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/RPC%E9%80%9A%E8%AE%AF%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83](https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/RPC%E9%80%9A%E8%AE%AF%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83 "协议规范")


## 使用示例 ##

环境要求：JDK 6+
```xml
<dependency>
  <groupId>com.baidu</groupId>
  <artifactId>jprotobuf-rpc-socket</artifactId>
  <version>2.0</version>
</dependency>
```

#### Qucik Start ####
Jprotobuf-rpc-socket基于JProtobuf基础上开发，可帮助大家开发中省去编写Google Protobuf的IDL描述文件的过程。

##### 客户端开发 #####
1.EchoService功用实现

EchoService 提供一个echo方法 ，参数对象EchoInfo只有一个message属性。
下面是EchoInfo对象定义
```java
public class EchoInfo {
    
    @Protobuf
    public String message;
}

```
注解方式的定义可以极大简化大家的工作量，上面等同于下面的IDEL配置
```property
package pkg;  

option java_package = "com.baidu.bjf.remoting.protobuf.rpc";

//这里声明输出的java的类名  
option java_outer_classname = "EchoInfo";  

message InterClassName {  
  required string message = 1;
} 


```
2.定义EchoService接口
```java
public interface EchoService {

    /**
     * To define a RPC client method. <br>
     * serviceName is "echoService"
     * methodName is use default method name "echo"
     * onceTalkTimeout is 200 milliseconds
     * 
     * @param info
     * @return
     */
    @ProtobufPRC(serviceName = "echoService", onceTalkTimeout = 200)
    EchoInfo echo(EchoInfo info);
}

```
RPC的方法必须要指定@ProtobufRPC注解. serviceName与methodName要与服务端保持一致。
这里未指定methodName，则使用方法的名称 "echo"


3.创建RPC Client进行访问
```java
RpcClient rpcClient = new RpcClient();
// 创建EchoService代理
ProtobufRpcProxy<EchoService> pbrpcProxy = new ProtobufRpcProxy<EchoService>(rpcClient, EchoService.class);
pbrpcProxy.setPort(1031);
// 动态生成代理实例
EchoService echoService = pbrpcProxy.proxy();
EchoInfo request = new EchoInfo();
request.message = "hello";
EchoInfo response = echoService.echo(request);
rpcClient.stop();
```

##### 服务端开发 #####
1.开发服务实现类
```java
public class EchoServiceImpl {

    @ProtobufPRCService(serviceName = "echoService", methodName = "echo")
    public EchoInfo doEcho(EchoInfo info) {
        EchoInfo ret = new EchoInfo();
        ret.setMessage("hello:" + info.message);
        
        return ret;
    }
}
```
服务发布的RPC方法必须用@ProtobufPRCService注解进行标识

2.发布RPC服务
```java
	RpcServer rpcServer = new RpcServer();
	
	EchoServiceImpl echoServiceImpl = new EchoServiceImpl();
	rpcServer.registerService(echoServiceImpl);
	rpcServer.start(1031);
```
上面的代码实现把 EchoServiceImpl 的RPC服务发布出去


## 性能测试 ##

机器配置：
- Linux 64G内存 6核 12线程 
- Intel(R) Xeon(R) CPU           E5645  @ 2.40GHz

性能测试结果如下：
平均QPS: 20000+
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