package com.transactionmq.test.example.service;

public interface CommonService {

    /**
     * 事务场景下的消息场景
     */
    void sendMessage();

    /**
     * 非事务场景下的消息场景
     */
    void sendMessageNoTran();

    /**
     * 带事务，且事务回滚的消息场景
     */
    void sendMessageRollback();

    /**
     * 消息重试，定时任务可调用该接口
     */
    void messageRetry();

}
