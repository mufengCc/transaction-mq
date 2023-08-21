package com.transactionmq.starter.param;

import com.transactionmq.starter.enums.EnumMessageSendModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class MessageParam implements Serializable {

    /**
     * 服务名称 eg:订单服务
     */
    private String service;

    /**
     * 业务名称 eg:创建订单
     */
    private String business;

    /**
     * topic
     */
    private String topic;

    /**
     * tag
     */
    private String tags;

    /**
     * 消息内容
     */
    private String body;

    /**
     * 消息key
     * 可作为检索条件,方便检索日志
     * 可作为顺序消息key
     */
    private String msgKey;

    /**
     * 同步发送，异步发送，单向发送、顺序发送
     */
    private EnumMessageSendModel model = EnumMessageSendModel.ASYNC;

}
