package com.transactionmq.test.example.service;


import com.transactionmq.starter.core.MessageManager;
import com.transactionmq.starter.enums.EnumMessageSendModel;
import com.transactionmq.starter.param.MessageParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final MessageManager messageManager;

    @Transactional
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

    @Override
    public void sendMessageNoTran() {
        MessageParam messageParam = new MessageParam();
        messageParam.setTopic("order-topic");
        messageParam.setBody("HELLO WORD");
        messageManager.sendMessage(messageParam);
    }

    @Transactional
    @Override
    public void sendMessageRollback() {
        MessageParam messageParam = new MessageParam();
        messageParam.setTopic("order-topic");
        messageManager.sendMessage(messageParam);
        log.info("出现异常，不会发送mq消息");
        throw new RuntimeException("异常");
    }

    @Override
    public void messageRetry() {
        messageManager.messageRetry();
        log.info("消息重试完成");
    }

}
