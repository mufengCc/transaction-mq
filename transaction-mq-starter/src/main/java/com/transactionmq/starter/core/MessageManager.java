package com.transactionmq.starter.core;

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

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    baseMessageSender.sendMessage(messageParam);
                }
            });
            log.info("【rocketMQ】发送消息,msgKey:{},初始化完成", messageParam.getMsgKey());
        } else {
            baseMessageSender.sendMessage(messageParam);
        }
    }

    /**
     * 消息发送失败的重试逻辑
     */
    public void messageRetry() {
        baseMessageSender.messageRetry();
    }

}
