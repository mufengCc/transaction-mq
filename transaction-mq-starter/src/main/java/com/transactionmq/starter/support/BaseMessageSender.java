package com.transactionmq.starter.support;

import com.alibaba.fastjson.JSON;
import com.transactionmq.starter.entity.LocalMessageRecordEntity;
import com.transactionmq.starter.enums.EnumMessageStatus;
import com.transactionmq.starter.mq.RocketMQProducer;
import com.transactionmq.starter.param.MessageParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;

import java.util.Date;

@Slf4j
public class BaseMessageSender {

    private final DefaultMessageMapper defaultMessageMapper;
    private final RocketMQProducer producer;
    private final Integer maxRetryTimes;

    public BaseMessageSender(DefaultMessageMapper defaultMessageMapper, RocketMQProducer producer, Integer maxRetryTimes) {
        this.defaultMessageMapper = defaultMessageMapper;
        this.producer = producer;
        this.maxRetryTimes = maxRetryTimes;
    }

    public void sendMessage(MessageParam config) {

        LocalMessageRecordEntity entity = LocalMessageRecordEntity.convertToEntity(config, maxRetryTimes);
        int pkId = defaultMessageMapper.save(entity);
        entity.setId(pkId);

        try {

            log.info("【rocketMQ】发送消息,msgKey:{},请求参数:{}", config.getMsgKey(), JSON.toJSONString(config));

            SendResult sendResult = producer.sendMessage(config);

            entity.setMsgId(sendResult != null ? sendResult.getMsgId() : "-1");
            entity.setStatus(EnumMessageStatus.SUCCESS.getCode());
            entity.setSendSuccessTime(new Date());
            defaultMessageMapper.updateSuccess(entity);

            log.info("【rocketMQ】发送消息,msgKey:{},消息ID:{},发送成功", config.getMsgKey(), sendResult.getMsgId());

        } catch (Exception e) {
            e.printStackTrace();

            // 消息发送失败
            defaultMessageMapper.updateFailed(entity);

            log.error("【rocketMQ】发送消息失败,msgKey:{},异常原因:", config.getMsgKey(), e);
        }

    }


}
