package com.transactionmq.starter.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.transactionmq.starter.enums.EnumMessageStatus;
import com.transactionmq.starter.param.MessageParam;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class LocalMessageRecordEntity implements Serializable {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 服务名称 eg:订单服务
     */
    private String service;

    /**
     * 业务名称 eg:创建订单
     */
    private String business;

    /**
     * 消息模式：SYNC，ASYNC，ONEWAY
     */
    private String model;

    /**
     * topic
     */
    private String topic;

    /**
     * tag
     */
    private String tags;


    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息key
     */
    private String msgKey;

    /**
     * 消息内容
     */
    private String body;

    /**
     * 发送状态  0:发送中  1:重试中  2:发送失败  3:发送成功
     */
    private Integer status;

    /**
     * 最大重试次数 -1无限重试
     */
    private Integer maxRetryTimes = -1;

    /**
     * 当前重试次数
     */
    private Integer currentRetryTimes;

    /**
     * 发送成功时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date sendSuccessTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date updateTime;


    public static LocalMessageRecordEntity convertToEntity(MessageParam config, Integer maxRetryTimes) {
        return new LocalMessageRecordEntity()
                .setService(config.getService())
                .setBusiness(config.getBusiness())
                .setModel(config.getModel().getCode())
                .setTopic(config.getTopic())
                .setTags(config.getTags())
                .setBody(config.getBody())
                .setStatus(EnumMessageStatus.SENDING.getCode())
                .setMaxRetryTimes(maxRetryTimes)
                .setCreateTime(new Date());
    }

}
