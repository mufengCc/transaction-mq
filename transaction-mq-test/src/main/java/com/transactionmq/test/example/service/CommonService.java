package com.transactionmq.test.example.service;

public interface CommonService {

    void sendMessage();

    void sendMessageRollback();

    void sendMessageNoTran();

    void sendMqMessage();


}
