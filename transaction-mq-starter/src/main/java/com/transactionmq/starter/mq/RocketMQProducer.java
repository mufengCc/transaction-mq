package com.transactionmq.starter.mq;


import com.transactionmq.starter.param.MessageParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class RocketMQProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public RocketMQProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public SendResult sendMessage(MessageParam config) throws Exception {
        switch (config.getModel()) {
            case SYNC -> {
                return syncSendMessage(config);
            }
            case ASYNC -> {
                return asyncSendMessage(config);
            }
            case ONEWAY -> {
                onewaySendMessage(config);
                return null;
            }
            case ORDER -> {
                return asyncSendOrderMessage(config);
            }
        }
        return null;
    }

    /**
     * 异步发送消息
     */
    public SendResult asyncSendMessage(MessageParam config) throws Exception {

        CompletableFuture<SendResult> future = new CompletableFuture<>();

        Message<String> message = buildMessage(config);
        rocketMQTemplate.asyncSend(getTopic(config), message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future.get();
    }

    public SendResult syncSendMessage(MessageParam config) {
        Message<String> message = buildMessage(config);
        return rocketMQTemplate.syncSend(getTopic(config), message);
    }

    public void onewaySendMessage(MessageParam config) {
        Message<String> message = buildMessage(config);
        rocketMQTemplate.sendOneWay(getTopic(config), message);
    }


    public SendResult asyncSendOrderMessage(MessageParam config) throws Exception {

        Message<String> message = buildMessage(config);

        CompletableFuture<SendResult> future = new CompletableFuture<>();

        /**
         * 顺序队列索引
         */
        rocketMQTemplate.setMessageQueueSelector((messageQueues, message1, arg) -> {
            int index = arg.hashCode();
            int queueCount = messageQueues.size();
            int selectedQueueIndex = index % queueCount;
            return messageQueues.get(selectedQueueIndex);
        });

        rocketMQTemplate.asyncSendOrderly(getTopic(config), message, config.getMsgKey(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future.get();
    }

    private static String getTopic(MessageParam config) {
        StringBuilder sb = new StringBuilder();
        sb.append(config.getTopic());
        if (ObjectUtils.isNotEmpty(config.getTags())) {
            sb.append(":").append(config.getTags());
        }
        return sb.toString();
    }

    private static Message<String> buildMessage(MessageParam config) {
        return MessageBuilder.withPayload(config.getBody())
                .setHeader(MessageConst.PROPERTY_KEYS, config.getMsgKey())
                .build();
    }

}
