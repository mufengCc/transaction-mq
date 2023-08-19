package com.transactionmq.starter.core;

import com.alibaba.fastjson.JSON;
import com.transactionmq.starter.param.MessageParam;
import com.transactionmq.starter.support.BaseMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
public class MessageManager {

    private final BaseMessageSender baseMessageSender;

    public MessageManager(BaseMessageSender baseMessageSender) {
        this.baseMessageSender = baseMessageSender;
    }

    public void sendMessage(MessageParam messageParam) {
        log.info("【rocketMQ】发送消息,msgKey:{},请求参数:{}", messageParam.getMsgKey(), JSON.toJSONString(messageParam));

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    baseMessageSender.sendMessage(messageParam);
                }
            });
        } else {
            baseMessageSender.sendMessage(messageParam);
        }
    }

}
