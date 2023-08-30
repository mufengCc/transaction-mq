package com.transactionmq.starter.support;

import com.alibaba.fastjson.JSON;
import com.transactionmq.starter.entity.LocalMessageRecordEntity;
import com.transactionmq.starter.enums.EnumMessageStatus;
import com.transactionmq.starter.mq.RocketMQProducer;
import com.transactionmq.starter.param.MessageParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
public class BaseMessageSender {


    private static final String SEND = "发送消息";
    private static final String RETRY = "重试消息";

    private final DefaultMessageMapper defaultMessageMapper;
    private final RocketMQProducer producer;
    private final Integer maxRetryTimes;

    public BaseMessageSender(DefaultMessageMapper defaultMessageMapper, RocketMQProducer producer, Integer maxRetryTimes) {
        this.defaultMessageMapper = defaultMessageMapper;
        this.producer = producer;
        this.maxRetryTimes = maxRetryTimes;
    }

    public void sendMessage(MessageParam param) {

        LocalMessageRecordEntity entity = addDbData(param);

        sendMessageCommon(param, entity, SEND);

    }

    private LocalMessageRecordEntity addDbData(MessageParam param) {
        LocalMessageRecordEntity entity = LocalMessageRecordEntity.convertToEntity(param, maxRetryTimes);
        int pkId = defaultMessageMapper.save(entity);
        entity.setId(pkId);
        log.info("【rocketMQ】发送消息,msgKey:{},主键id:{},落库完成", param.getMsgKey(), pkId);
        return entity;
    }

    public void messageRetry() {
        List<LocalMessageRecordEntity> recordEntities = defaultMessageMapper.selectFailed(new LocalMessageRecordEntity().setStatus(EnumMessageStatus.RETRYING.getCode()));

        if (CollectionUtils.isEmpty(recordEntities)) {
            log.info("【rocketMQ】重试消息,无任何需要重试的数据");
            return;
        }

        recordEntities.forEach(entity -> {
            log.info("【rocketMQ】重试消息,msgKey:{},开始重试", entity.getMsgKey());
            MessageParam messageParam = LocalMessageRecordEntity.convertToMessageParam(entity);
            sendMessageCommon(messageParam, entity, RETRY);
            log.info("【rocketMQ】重试消息,msgKey:{},重试完成", entity.getMsgKey());
        });

    }

    private void sendMessageCommon(MessageParam param, LocalMessageRecordEntity entity, String scene) {
        try {

            log.info("【rocketMQ】{},msgKey:{},请求参数:{}", scene, param.getMsgKey(), JSON.toJSONString(param));

            SendResult sendResult = producer.sendMessage(param);

            entity.setMsgId(sendResult != null ? sendResult.getMsgId() : "-1");
            entity.setStatus(EnumMessageStatus.SUCCESS.getCode());
            entity.setSendSuccessTime(new Date());
            defaultMessageMapper.updateSuccess(entity);

            log.info("【rocketMQ】{},msgKey:{},消息ID:{},发送成功", scene, param.getMsgKey(), sendResult.getMsgId());

        } catch (Exception e) {
            e.printStackTrace();

            // 消息发送失败
            defaultMessageMapper.updateFailed(entity);

            log.error("【rocketMQ】{}失败,msgKey:{},异常原因:", scene, param.getMsgKey(), e);
        }
    }


}
