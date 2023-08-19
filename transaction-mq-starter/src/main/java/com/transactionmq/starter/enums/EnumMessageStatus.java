package com.transactionmq.starter.enums;

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum EnumMessageStatus {

    SENDING(0, "发送中"),
    RETRYING(1, "重试中"),
    FAILED(2, "发送失败"),
    SUCCESS(3, "发送成功"),
    ;

    private Integer code;
    private String name;

    EnumMessageStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
