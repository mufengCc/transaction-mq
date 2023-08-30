# 本地消息表实现分布式事务
### 前言

作用：解决mq消息丢失及分布式事务问题

实现思路：利用spring事务管理的监听器，保证事务提交之后，消息才会推送。

**运行版本**
* Spring版本：3.1.2
* JDK版本： 17

### 开始使用
* spingboot项目引用jar包
* 创建本地消息表
* 使用示例
* 数据库验证

**1.创建本地消息表**
~~~
CREATE TABLE `local_message_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `service` varchar(50) DEFAULT NULL COMMENT '服务名称 eg:订单服务',
  `business` varchar(50) DEFAULT NULL COMMENT '业务名称 eg:创建订单',
  `model` varchar(50) NOT NULL COMMENT '消息模式：SYNC，ASYNC，ONEWAY',
  `topic` varchar(50) NOT NULL COMMENT 'topic',
  `tags` varchar(50) DEFAULT NULL COMMENT 'tag',
  `msg_id` varchar(50) DEFAULT NULL COMMENT '消息id',
  `msg_key` varchar(50) DEFAULT NULL COMMENT '消息key',
  `body` text NOT NULL COMMENT '消息体',
  `status` tinyint(5) NOT NULL DEFAULT '0' COMMENT '发送状态  0:发送中  1:重试中  2:发送失败  3:发送成功',
  `max_retry_times` tinyint(5) NOT NULL DEFAULT '-1' COMMENT '最大重试次数',
  `current_retry_times` tinyint(5) DEFAULT NULL COMMENT '当前重试次数',
  `send_success_time` datetime(3) DEFAULT NULL COMMENT '发送成功时间',
  `create_time` datetime(3) NOT NULL COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
~~~

**2.开始使用**
~~~
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final MessageManager messageManager;

    public void sendMessage() {
        MessageParam messageParam = new MessageParam();
        messageParam.setService("订单服务");
        messageParam.setBusiness("创建订单");
        messageParam.setModel(EnumMessageSendModel.ASYNC);
        messageParam.setTopic("order-topic");
        messageParam.setTags("xx");
        messageParam.setMsgKey("No20001");
        messageParam.setBody("HELLO TRANSACTION-MQ");
        messageManager.sendMessage(messageParam);
    }

}
~~~
**3.数据库验证**
~~~
mysql> select * from local_message_record;
+----+----------+----------+-------+-------------+------+----------------------------------+---------+----------------------+--------+-----------------+---------------------+-------------------------+-------------------------+-------------------------+
| id | service  | business | model | topic       | tags | msg_id                           | msg_key | body                 | status | max_retry_times | current_retry_times | send_success_time       | create_time             | update_time             |
+----+----------+----------+-------+-------------+------+----------------------------------+---------+----------------------+--------+-----------------+---------------------+-------------------------+-------------------------+-------------------------+
|  1 | 订单服务 | 创建订单 | ASYNC | order-topic | xx   | 7F0000017DA863947C6B56C66BC00000 | No20001  | HELLO TRANSACTION-MQ |      3 |              -1 | NULL                | 2023-08-17 20:24:04.293 | 2023-08-17 20:24:04.257 | 2023-08-17 20:24:25.232 |
+----+----------+----------+-------+-------------+------+----------------------------------+---------+----------------------+--------+-----------------+---------------------+-------------------------+-------------------------+-------------------------+
1 row in set (0.02 sec)
~~~

### 消息重试

* 定时任务可轮询该接口，会将发送失败的消息重试，直到消息发送成功

~~~
public void messageRetry() {
        messageManager.messageRetry();
        log.info("消息重试完成");
  }
~~~

### TODO
1. [x] maven中心仓库推送
2. [x] 支持扩展本地消息表
3. [x] 支持配置覆盖，满足自定义场景
4. [x] 支持其它厂商mq
