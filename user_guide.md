#### Qucik Start ####
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

#### 附件功能 ####
由于protobuf不适合大数据的序列化传输，针对附件需求的提供的方案如下：

##### 客户端开发 #####
```java
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 450, 
            attachmentHandler = EchoClientAttachmentHandler.class, logIDGenerator = EchoLogIDGenerator.class)
    EchoInfo echoWithAttachement(EchoInfo info);
```
EchoClientAttachmentHandler实现示例如下：
```java
public class EchoClientAttachmentHandler implements ClientAttachmentHandler {

    private byte[] attachment = EchoClientAttachmentHandler.class.getName().getBytes();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.AttachmentHandler#handleRequest(java.lang.String
     * , java.lang.String, java.lang.Object[])
     */
    public byte[] handleRequest(String serviceName, String methodName, Object... params) {
        return attachment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.ClientAttachmentHandler#handleResponse(byte[],
     * java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void handleResponse(byte[] response, String serviceName, String methodName, Object... params) {
        Assert.assertEquals(EchoServerAttachmentHandler.class.getName(), new String(response));

    }

}
```
##### 服务端开发 #####
```java
    @ProtobufRPCService(serviceName = "echoService", methodName = "echoWithAttachement", 
            attachmentHandler = EchoServerAttachmentHandler.class)
    public EchoInfo dealWithAttachement(EchoInfo info) {
        return doEcho(info);
    }
```
EchoServerAttachmentHandler实现示例如下：
```java
public class EchoServerAttachmentHandler implements ServerAttachmentHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.jprotobuf.pbrpc.ServerAttachmentHandler#handleAttachement(byte
     * [], java.lang.String, java.lang.String, java.lang.Object[])
     */
    public byte[] handleAttachement(byte[] response, String serviceName, String methodName, Object... params) {
        Assert.assertEquals(EchoClientAttachmentHandler.class.getName(), new String(response));
        return EchoServerAttachmentHandler.class.getName().getBytes();
    }

}
```

#### 压缩功能支持 ####

开启压缩功能比较简单
只需要在客户端指定压缩设置即可，目前支持GZIP和SNAPPY两种

```java
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 1500, compressType = CompressType.GZIP,
            attachmentHandler = EchoClientAttachmentHandler.class, logIDGenerator = EchoLogIDGenerator.class)
    EchoInfo echoGzip(EchoInfo info);
    
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 1500, compressType = CompressType.Snappy,
            attachmentHandler = EchoClientAttachmentHandler.class, logIDGenerator = EchoLogIDGenerator.class)
    EchoInfo echoSnappy(EchoInfo info);
```

#### 负载均衡功能支持 ####
该功能在2.16版本之后支持，由HaProtobufRpcProxy 支持

HaProtobufRpcProxy的服务列表需要NamingService接口支持
```java
public interface NamingService {

    /**
     * get server list from naming service.
     * @return server list.
     * @throws Exception in case of any exception
     */
    List<InetSocketAddress> list() throws Exception;
  
}
```

下面是一个使用示例：
HaProtobufRpcProxy 的构建非常简单，代码如下：

```java
HaProtobufRpcProxy<EchoService> pbrpcProxy =
                    new HaProtobufRpcProxy<EchoService>(rpcClient, EchoService.class, getNamingService());
EchoService proxy = pbrpcProxy.proxy();
```
重要说明：
上面的代码实现没有指定负载方式，则使用轮循方式访问，权重都为1. 负载过程中如果出现某台服务出现异常，则会从队列中摘出，后台启动心跳检测程序，默认1秒一次。检测方式则只是ping接口方式。


为了方便测试，下面通过一个模拟的NamingService接口实现
```java
public class DummyNamingService implements NamingService {
    
    private List<InetSocketAddress> list;
    
    /**
     * @param list
     */
    public DummyNamingService(List<InetSocketAddress> list) {
        super();
        this.list = list;
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.client.ha.NamingService#list()
     */
    public List<InetSocketAddress> list() throws Exception {
        return list;
    }

}
		// 使用示例
        address = new InetSocketAddress(1035);
        list.add(address);

        namingService = new DummyNamingService(list);

```

更多示例请参见 单元测试代码：com.baidu.jprotobuf.pbrpc.client.ha.HaEchoServiceTest


#### Spring集成支持 ####
该功能在2.17版本之后支持, 支持xml配置方式和注解方式两种。
##### xml配置方式说明 #####
**使用RpcServiceExporter暴露服务RPC server服务<br>**
使用RpcServiceExporter，我们可以把EchoServiceImpl对象暴露成RPC服务。可以使用 RpcProxyFactoryBean 或API的方式来访问该接口。

首先使用@ProtobufRPCService在我们需要发布成服务的方法进行配置

```java
public class EchoServiceImpl {
    @ProtobufRPCService(serviceName = "echoService", methodName = "echo")
    public EchoInfo doEcho(EchoInfo info) {
        EchoInfo ret = new EchoInfo();
        ret.setMessage("hello:" + info.getMessage());
        return ret;
    }
```

下面演示使用RpcServiceExporter来发布服务
```xml
	<bean id="echoService" class="com.baidu.jprotobuf.pbrpc.EchoServiceImpl"></bean>

	<bean class="com.baidu.jprotobuf.pbrpc.spring.RpcServiceExporter">
		<property name="servicePort" value="1031"></property>
		<property name="registerServices">
			<list>
				<ref local="echoService" />
			</list>
		</property>
        <property name="connectTimeout" value="1000"></property>
	</bean>

```
上面的xml配置实现把EchoServiceImpl类的 doEcho方法发布成rpc服务。 服务名称:echoService 服务方法:echo 端口 1031

注：RpcServiceExporter已经继承了RpcServiceOptions类，可以把所有RPC的额外配置属性
通过属性方式进行配置.

**使用RpcProxyFactoryBean进行客户端连接<br>**
针对上面发布的服务，我们可以单独编写一个接口，代码如下：
```java
public interface EchoService {
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 1000)
    EchoInfo echo(EchoInfo info);
}
```

为了把服务客户端连接到上，我们将创建一个单独的Spring容器，包含这个简单对象和链接配置位的服务：
```xml
	<bean id="echoServiceProxy" class="com.baidu.jprotobuf.pbrpc.spring.RpcProxyFactoryBean">
		<property name="serviceInterface" value="com.baidu.jprotobuf.pbrpc.EchoService"></property>
		<property name="port" value="1031"></property>
	</bean>
```

这样Spring会动态创建一个bean名称为echoServiceProxy，接口类型为EchoService的代理类。

注：RpcProxyFactoryBean已经继承了RpcClientOptions类，可以把所有RPC的额外配置属性
通过属性方式进行配置.

**使用HaRpcProxyFactoryBean进行客户端连接<br>**
HaRpcProxyFactoryBean是对负载均衡功能的Spring集成支持。 配置也是非常简单，示例如下：

```xml
	<bean id="namingService" class="com.baidu.jprotobuf.pbrpc.spring.UrlBasedNamingService">
		<constructor-arg>
			<value>localhost:1031;localhost:1032;localhost:1033</value>
		</constructor-arg>
	</bean>


	<bean id="echoServiceProxy" class="com.baidu.jprotobuf.pbrpc.spring.HaRpcProxyFactoryBean">
		<property name="serviceInterface" value="com.baidu.jprotobuf.pbrpc.EchoService"></property>
		<property name="namingService" ref="namingService"></property>
	</bean>
```


##### 注解配置方式说明 #####
注解配置方式要比xml的配置简化很多，这也是我们更推荐的一种发布方式

首先无论是发布服务还是声明客户端连接，都只需要配置一次以下信息

```xml
	<bean
		class="com.baidu.jprotobuf.pbrpc.spring.annotation.CommonAnnotationBeanPostProcessor">
		<property name="callback">
			<bean
				class="com.baidu.jprotobuf.pbrpc.spring.annotation.ProtobufRpcAnnotationResolver"></bean>
		</property>
	</bean>
```

下面的配置是推荐给大家，以更好的使用注解发布方式

```xml

	<context:component-scan base-package="com.baidu.jprotobuf.pbrpc.spring">
	</context:component-scan>

	<bean
		class="com.baidu.jprotobuf.pbrpc.spring.annotation.CommonAnnotationBeanPostProcessor">
		<property name="callback">
			<bean
				class="com.baidu.jprotobuf.pbrpc.spring.annotation.ProtobufRpcAnnotationResolver"></bean>
		</property>
	</bean>


```

**使用@RpcExporter暴露服务RPC server服务<br>**
```java
@Component
@RpcExporter(port = "1031")
public class EchoServiceImpl {
    @ProtobufRPCService(serviceName = "echoService", methodName = "echo")
    public EchoInfo doEcho(EchoInfo info) {
        EchoInfo ret = new EchoInfo();
        ret.setMessage("hello:" + info.getMessage());
        return ret;
    }

```

**使用@RpcProxy进行客户端连接<br>**
```java
@Service("echoServiceClient")
public class AnnotationEchoServiceClient {

    @RpcProxy(port = "1031", host = "127.0.0.1", serviceInterface = EchoService.class, lookupStubOnStartup = false)
    public EchoService echoService;
}

public interface EchoService {
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 1000)
    EchoInfo echo(EchoInfo info);
}
```

**使用@HaRpcProxy进行负载均衡客户端连接<br>**
注：使用HaRpcProxy必须需要通过NamingService接口提供服务列表，注解方式则需要在xml中进行配置，然后在注解中指定

```xml
	<bean id="namingService" class="com.baidu.jprotobuf.pbrpc.spring.UrlBasedNamingService">
		<constructor-arg>
			<value>localhost:1031;localhost:1032;localhost:1033</value>
		</constructor-arg>
	</bean>
```

```java
@Service("echoServiceClient")
public class AnnotationEchoServiceClient {

    @HaRpcProxy(namingServiceBeanName = "namingService", serviceInterface = EchoService.class,
            lookupStubOnStartup = false)
    public EchoService haEchoService;
}

public interface EchoService {
    @ProtobufRPC(serviceName = "echoService", onceTalkTimeout = 1000)
    EchoInfo echo(EchoInfo info);
}
```

#### Redis注册服务使用 ####
jprotobuf-rpc支持使用Redis来实现服务的注册与发现功能

示例配置如下：
```xml
    <bean id="namingService" class="com.baidu.pbrpc.register.redis.RedisRegistryService">
       <constructor-arg>
           <bean class="com.baidu.pbrpc.register.redis.RedisClient">
              <property name="redisServer" value="localhost"></property>
              <property name="port" value="6379"></property>
              <property name="testOnBorrow" value="true"></property>
              <property name="maxWait" value="2000"></property>
           </bean>
       </constructor-arg>
       <property name="administrator" value="true"></property>
       <property name="group" value="default/"></property>
<property name="expirePeriod" value="3000"></property>
</bean>

```
RedisRegistryService属性：
1.	expirePeriod 服务过期时间设置，当服务注册成功后，会写入最后的注册时间，然后会在定期更新服务存活时间(频率 expirePeriod/3)
2.	administrator， 默认是false, 当设置为true时，会对redis上已经注册的服务已经过期的时间进行删除。 一般有一台服务进行清理即可，也可支持多台一起清理
3.	group 表示分组，可以同一组名下的服务才有能力相互发现


服务的发布时使用示例
```xml
  <bean class="com.baidu.jprotobuf.pbrpc.spring.RpcServiceExporter">
        <property name="servicePort" value="1031"></property>
        <property name="registerServices">
            <list>
                <ref local="echoService" />
            </list>
        </property>
		<property name="registryCenterService" ref="namingService">
        <property name="connectTimeout" value="1000"></property>
    </bean>

```

客户端使用示例
```xml
<bean id="echoServiceProxy" class="com.baidu.jprotobuf.pbrpc.spring.HaRpcProxyFactoryBean">
        <property name="serviceInterface" value="com.baidu.jprotobuf.pbrpc.EchoService"></property>
        <property name="namingService" ref="namingService"></property>
    </bean>

```

#### HTTP查看支持 ####
该功能在3.1.1版本之后支持配置方式如下：
1.       代码方式
```java 
   RpcServerOptions rpcServerOptions = new RpcServerOptions();
   rpcServerOptions.setHttpServerPort(8866);
       
    RpcServer rpcServer = new RpcServer(rpcServerOptions);
 ```
2.       Spring xml方式
```xml 
    <bean class="com.baidu.jprotobuf.pbrpc.spring.RpcServiceExporter">
        <property name="servicePort" value="1031"></property>
        <property name="registerServices">
            <list>
                <ref local="echoService" />
            </list>
        </property>
        <property name="connectTimeout" value="1000"></property>
        <property name="httpServerPort" value="8866"></property>
    </bean>
 ```
 
3.       Spring注解方式
```java
@RpcExporter(port = "1033" , rpcServerOptionsBeanName = "rpcServerOptions")
public class AnnotationEchoServiceImpl3 extends EchoServiceImpl {
        
             <bean id="rpcServerOptions" class="com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions">
        <property name="acceptorThreads" value="1"></property>
        <property name="workThreads" value="20"></property>
        <property name="httpServerPort" value="8866"></property>
        </bean>
 ```java
 

更多使用示例参见 单元测试com.baidu.jprotobuf.pbrpc.spring.AnnotationRpcXmlConfigurationTest
