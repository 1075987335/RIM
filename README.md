# RIM

## 介绍

`RIM`是基于Netty实现的面相开发者的高性能分布式即时通信系统，保证消息的实时性、有序性、可靠性。

## 技术栈

| 名称         | 作用                                                         |
| ------------ | ------------------------------------------------------------ |
| SpringBoot   | 利用IOC特性简化开发                                          |
| Mybaits-plus | 简化与mysql的交互过程                                        |
| Netty        | 高性能消息收发、心跳检测、应用层ACK                          |
| Redis        | 保存用户会话信息、去重信息、群聊单聊的离线消息以及其他信息的缓存 |
| Mysql        | 持久化信息                                                   |
| RabbitMQ     | 将存储操作异步，利用RabbitMQ的可靠性机制返回ACK              |
| Zookeeper    | 注册中心、感知服务节点变化情况                               |
| Dubbo        | 在路由层利用泛化调用实现定向功能调用，以及服务之间方法调用   |
| Kryo         | 序列化协议                                                   |

## 模块结构

| 模块   | 作用                                                         |
| ------ | ------------------------------------------------------------ |
| Client | 客户端：负责接入IM系统、应用层ACK                            |
| Router | 路由层：负责消息（群聊、单聊、离线）转发、用户回话信息管理、应用层ACK |
| Server | 服务层：消息推送                                             |
| Store  | 存储层：消息（群聊、私聊）持久化、离线消息查询、应用层ACK    |

## 亮点

+ 设计模式
  + 使用策略模式实现Client的各种内置命令、Netty接收消息处理、Router转发逻辑等功能
+ 使用读扩散实现群聊离线消息拉取
+ 使用RabbitMQ的可靠性机制为客户端返回ACK、异步执行消息持久化
+ 使用Dubbo的泛化调用机制实现服务的定向调用，解决了因分布式IM_Server的部署导致的用户信息分散在不同服务器上的问题
+ 解决了自定义协议在传输中导致的粘包、拆包问题

+ 群聊批量ACK处理，避免因创建过多的超时计时器导致的压力过大

+ 利用zookeeper的watch机制监听Router和IM_Server的变化分别回调给客户端和Router做相应处理
+ 使用时间轮算法实现了ACK等待列表功能
+ 客户端断线重连、服务端自动剔除离线用户
+ 三大特性
  + 实时性：使用Netty、线程池、RabbitMQ、Dubbo大幅提升消息实时性
  + 可靠性：使用应用层ACK、消息去重、离线消息确保消息可靠性
  + 有序性：利用每个单聊和群聊会话相对递增的ID以及接收端的消息整流机制实现有序性

## 架构设计

### 私有协议

+ 普通消息（群聊、私聊）

| 字段名 ： 类型 大小               | 含义                   |
| --------------------------------- | ---------------------- |
| `Constants.HEAD_DATA` : int 4byte | 消息标志               |
| `total_length` : int 4byte        | 下面所有数据的字节长度 |
| `type` : byte 1byte               | 消息类型               |
| `header_length` : int 4byte       | header长度             |
| `body_length` : int 4byte         | body长度               |
| `message_type` : int 4byte        | 消息的序列化类型       |
| `header`                          | 消息头                 |
| `body`                            | 消息体                 |

+ 心跳消息

| 字段名 ： 类型 大小               | 含义                   |
| --------------------------------- | ---------------------- |
| `Constants.HEAD_DATA` : int 4byte | 消息标志               |
| `total_length` : int 4byte        | 下面所有数据的字节长度 |
| `type` : byte 1byte               | 消息类型               |

+ ACK消息

| 字段名 ： 类型 大小               | 含义                   |
| --------------------------------- | ---------------------- |
| `Constants.HEAD_DATA` : int 4byte | 消息标志               |
| `total_length` : int 4byte        | 下面所有数据的字节长度 |
| `type` : byte 1byte               | 消息类型               |
| `MID` : long 8byte                | 消息ID                 |

+ 登陆消息

| 字段名 ： 类型 大小               | 含义                   |
| --------------------------------- | ---------------------- |
| `Constants.HEAD_DATA` : int 4byte | 消息标志               |
| `total_length` : int 4byte        | 下面所有数据的字节长度 |
| `type` : byte 1byte               | 消息类型               |
| `UID` : long 8byte                | 用户ID                 |

### 消息投递过程

#### 单聊

![image-20230524210952029](Cim总结2.0.assets/image-20230524210952029.png)

#### 群聊

![image-20230524211307330](Cim总结2.0.assets/image-20230524211307330.png)

#### 登陆

![image-20230524225942421](Cim总结2.0.assets/image-20230524225942421.png)

#### 可靠性模型

在设计上，整体使用应用层ACK+超时重传+消息去重+离线消息来完成。

##### 应用层ACK、超时重传

超时计时器使用时间轮算法实现，ACK消息除了发送端和接收端发送的ACK之外还用到了RabbitMQ的手动确认模式，用其回调函数做一些处理。

+ 单聊

  + 客户端发送一条消息后，会将此消息加入ACK等待列表中，并且在时间轮中执行一个重发任务，每一次任务执行都会去判断ACK等待列表中是否还存在此任务，如果没有，则进行重发。隔一段时间重发指定次数，如果收到了ACK，先将消息从ACK等待列表中移除，然后取消在时间轮中的重传任务。

  + 路由层收到客户端发送的消息之后会将消息放入MQ中进行持久化操作，等存储服务存储完成后会进行`basicAck`，然后会触发路由层的回调方法，在回调方法中会向原客户端发送Ack表示消息已成功存储。

    然后路由层会远程调用相应服务器进行消息的推送，并且同样开启ACK等待列表，时间轮放入重传任务。（如果达到了重传次数，会将这个消息转换为离线消息放到MQ中进行存储，在后面单独介绍。）

  + 接收方收到消息后会发送ACK消息，此时路由层会将这个ACK放入MQ中给存储服务进行完后续处理后进行`basicAck`，触发回调方法，将消息从等待列表中移除，并且取消时间轮中的重传任务。

+ 群聊

  + 与单聊流程差不多，区别就是离线消息存储在路由层接收到消息后就存储了，并且如果重传任务次数达到了也不会进行离线消息存储，而是直接提示失败。

##### 离线消息模型

+ 单聊

  + 用户在发送消息到路由层，一开始是不会保存离线消息的，只有当目标用户不在线或者发送失败（达到重传次数）后才会进行保存。在redis中，使用ZSet结构，score为消息id，value为消息内容；之前每接收一条单聊消息都会在mysql中的好友表更新最新接收消息的id。
  + 当目标用户上线后，会去mysql里面查找最新接收的消息id，然后去redis里面查找，发送所有id大于此id的消息给用户。
  + 等用户收到消息并且成功返回Ack后，会删除这些离线消息，并且修改最新接收消息id。

+ 群聊

  + 用户在发送群聊消息到路由层后，直接在redis里面保存离线消息。在mysql中会保存一张群成员表，表中会记录所有群成员接收的最新消息的id。

  + 当目标用户上线后，会去mysql里面查找最新接收的消息id，然后去redis里面查找，发送所有id大于此id的消息给用户。
  + 等用户收到消息并且成功返回ack后，修改最新接收消息id

##### 消息去重模型

+ 客户端
  + 使用一个`ReceivedMessage`类来保存相应消息id和接收的时间。
  + 当客户端开启的时候，就开启一个后台线程，定时检查这个类中的消息，查看`当前时间 - 消息接收的时间`是否大于给定时间，如果大于，就将去重消息去除，如果小于就跳过。
+ 路由层
  + 会查看这个消息在redis中是否存在。如果存在，说明消息重复了，不做处理；否则就正常处理
+ 存储层
  + 在消息持久化的时候会将消息保存到一份到redis中作为去重消息，设置一定的过期时间

#### 心跳、重连、剔除模型

##### 心跳

+ 

# 心跳、重连、剔除

## 客户端重连怎么实现的？

客户端在idleStateHandler里面添加写空闲，服务端添加读空闲。

+ 客户端在channelInactive里面实现断线重连逻辑
+ 使用ScheduledThreadPoolExecutor实现定时任务，可以自己设定执行次数，我设置的是3次
+ 如果重连都失败了，那么就关闭定时任务提示已掉线

# 群聊

## 功能流程

```mermaid
flowchart TD
	c1(客户端A发送消息)-->r(路由层接收消息)
	c1-->clientOuttime(消息ACK超时计时)
	clientOuttime-->outtime{是否超时}
	outtime-->retrycount{是否已到达重传次数}
	retrycount-->|no| clientOuttime
	retrycount-->|yes| fail(提示消息发送失败)
	
	r-->dup{消息是否重复}
	dup-->|yes| none(不做处理)
	dup-->|no| handle(推送消息)
	
	handle-->mq(消息队列)
	mq-->store(存储服务接收群聊消息)
	store-->db[("mysql持久化
	            redis保存群离线消息")]
	db-->storeAck(向客户端A返回ACK)
	handle-->queryOnline(查询本群所有在线成员)
	queryOnline-->route(获取每个在线用户的路由地址)
	route-->routeOuttime(消息ACK超时计时)
	route-->send(server推送消息)

	send-->cn(客户端N接收消息)
	cn-->clientAck(发送成功接收消息ACK)

	

```

# 内置命令

| 命令                   | 作用 |
| ---------------------- | ---- |
| :p2p [用户ID] [消息]   | 单聊 |
| :group [群组ID] [消息] | 群聊 |

# 服务器运行端口

| 服务名 | 绑定端口 | 网络端口 | Dubbo-protocol-port | Dubbo-qos-port |
| ------ | -------- | -------- | ------------------- | -------------- |
| client | 1000     | 500      | 1500                | 2000           |
| server | 1001     | 501      | 1502                | 2001           |
| route  |          | 502      | 1501                | 2002           |

# 数据库表单设计

+ user

| Field_Name | Type    | Declare        |
| ---------- | ------- | -------------- |
| id         | bigint  | 用户的唯一标识 |
| name       | varchar | 用户名         |

+ group

| Field_Name | Type    | Declare        |
| ---------- | ------- | -------------- |
| id         | bigint  | 群组的唯一标识 |
| name       | varchar | 群组名         |

+ user_group_list

| Field_Name        | Type   | Declare                          |
| ----------------- | ------ | -------------------------------- |
| id                | int    | 主键                             |
| user_id           | bigint | 用户id                           |
| gourp_id          | bigint | 用户加入的群组id                 |
| latest_message_id | bigint | 用户确认收到的最新的本群消息的id |

+ user_friend_list

| Field_Name        | Type   | Declare                                |
| ----------------- | ------ | -------------------------------------- |
| id                | int    | 主键                                   |
| user_id           | bigint | 用户id                                 |
| friend_id         | bigint | 好友id                                 |
| latest_message_id | bigint | 用户确认收到的最新的来自本好友的消息id |

+ p2p_message

| Field_Name | Type    | Declare      |
| ---------- | ------- | ------------ |
| id         | int     | 主键         |
| to         | bigint  | 接收方用户id |
| from       | bigint  | 发送方用户id |
| message_id | bigint  | 消息id       |
| state      | int     | 消息接收状态 |
| body       | varchar | 消息体       |

+ group_message

| Field_Name | Type    | Declare      |
| ---------- | ------- | ------------ |
| id         | int     | 主键         |
| group_id   | bigint  | 群组id       |
| from       | bigint  | 发送方用户id |
| message_id | bigint  | 消息id       |
| body       | varchar | 消息体       |

# Redis数据设计

```java
public static class RedisConstants {
        /**
         * 单聊离线消息
         * 格式：FromId:P2PMessage:ToId
         */
        public static final String UserP2PMessageStore = ":P2PMessage:";

        /**
         * 群聊离线消息
         * 格式：GroupMessage:GroupId
         */
        public static final String GorupMessageStore = "GroupMessage:";

        /**
         * 用户登陆状态
         * 格式：UserId:LoginState
         */
        public static final String LoginState = "LoginState";

        /**
         * 群成员列表
         * 格式：GroupId:GroupMember
         */
        public static final String GroupMember = ":GroupMember";

        /**
         * 单聊消息去重
         * 格式：P2PCacheMessage:msgId
         */
        public static final String P2PCacheMessage = "P2PCacheMessage:";

        /**
         * 群聊消息去重
         * 格式：groupId:GroupCacheMessage:msgId
         */
        public static final String GourpCacheMessage = ":GroupCacheMessage:";

        /**
         * 用户路由信息
         */
        public static final String UserRoute = ":userRoute";
    }
```

用opsForZset的时候返回的虽然是一个set，但是是有序的，因为这个set是redis自行实现的，可以保证有序。

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
