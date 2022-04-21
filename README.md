Jprotobuf-rpc-socket
====================

## Build status

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.baidu/jprotobuf-rpc-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.baidu/jprotobuf-rpc-core)


Protobuf RPC是一种基于TCP协议的二进制高性能RPC通信协议实现。它以Protobuf作为基本的数据交换格式，支持完全基于POJO的发布方式，极大的简化了开发复杂性。<br>
Features:<br>
- 完全支持POJO方式发布，使用非常简单
- 内置连接池，具备更高的性能，低延迟 QPS: 5w+
- 支持自动重连功能
- Client支持Ha的负载均衡功能
- 支持附件发送
- 压缩功能，支持GZip与Snappy
- 支持多包拆分与合并功能
- 支持多级超时设定，灵活控制请求超时时间
- 支持RPC服务元数据动态生成，简单易用
- 集成内置HTTP管理功能(3.1.1版本+)

关联项目：
[https://github.com/jhunters/jprotobuf](https://github.com/jhunters/jprotobuf "https://github.com/jhunters/jprotobuf")<br>
golang 协议实现: [https://github.com/baidu-golang/baidurpc](https://github.com/baidu-golang/baidurpc "https://github.com/baidu-golang/baidurpc")


## 使用示例 ##

环境要求：JDK 6+
```xml
<dependency> 
	<groupId>com.baidu</groupId>
	<artifactId>jprotobuf-rpc-core</artifactId>
	<version>4.2.1</version>
</dependency>

<!-- 提供spring扩展 -->
<dependency>
	<groupId>com.baidu</groupId>
	<artifactId>jprotobuf-rpc-core-spring</artifactId>
	<version>4.2.1</version>
</dependency>

<!-- 提供spring boot扩展 -->
<dependency>
	<groupId>com.baidu</groupId>
	<artifactId>jprotobuf-rpc-spring-starter</artifactId>
	<version>4.2.1</version>
</dependency>

<!-- 基于redis实现服务注册，发现功能 -->
<dependency>
	<groupId>com.baidu</groupId>
	<artifactId>jprotobuf-rpc-registry-redis</artifactId>
	<version>4.2.1</version>
</dependency>

```
使用Jprotobuf pre compile插件进行预编译，提升启动速度
```xml
    <plugin>
        <groupId>com.baidu</groupId>
        <artifactId>jprotobuf-precompile-plugin</artifactId>
        <version>1.2.8</version>
        <configuration>
            <skipErrorNoDescriptorsFound>true</skipErrorNoDescriptorsFound>
            <filterClassPackage>com.baidu</filterClassPackage>
        </configuration>
        <executions>
            <execution>
                <phase>compile</phase>
                <goals>
                    <goal>precompile</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
```
filterClassPackage 用来指定进行预编译时需要扫描的package,目前只支持配置一个package名称<br>
maven执行命令如下:<br>
```property
mvn jprotobuf:precompile
or
mvn package 
```
[下载发行包](http://repo1.maven.org/maven2/com/baidu/jprotobuf-rpc-core/)
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
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 200)
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

    @ProtobufRPCService(serviceName = "echoService", methodName = "echo")
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

[更多使用说明](https://github.com/Baidu-ecom/Jprotobuf-rpc-socket/wiki/User-Guide)

## 性能测试 ##

机器配置：
- Linux 64G内存 6核 12线程 
- Intel(R) Xeon(R) CPU           E5645  @ 2.40GHz

性能测试结果如下(客户端与服务端部署在同一台机器中)：
单线程：平均QPS: 9000+
多线程：QPS: 最高 40000+
```property
---------------------Performance Result-------------------------
send byte size: 44;receive byte size: 50
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|               11807|                     0|                8469|                   1|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 44;receive byte size: 50
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|               10407|                     0|                9608|                   1|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 1139;receive byte size: 1139
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|               11513|                     0|                8685|                   1|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 44;receive byte size: 50
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                5904|                     0|               16937|                   2|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 44;receive byte size: 50
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                3754|                     0|               26638|                   4|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 44;receive byte size: 50
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                1736|                     0|               57603|                  20|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 1139;receive byte size: 1139
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                2381|                     0|               41999|                  20|
---------------------Performance Result-------------------------
---------------------Performance Result-------------------------
send byte size: 1139;receive byte size: 1139
|         total count|       time took(ms)|           average(ms)|                 QPS|             threads|
|              100000|                2012|                     0|               49701|                  40|
---------------------Performance Result-------------------------
```

```property
License

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
 ```
 
 ### 沟通群号：QQ: 644867264 ###  进群口令 jprotobuf
 