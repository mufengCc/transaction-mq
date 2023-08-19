package com.transactionmq.starter.enums;

import lombok.Getter;

/**
 * 消息推送模式
 */
@Getter
public enum EnumMessageModel {


    CLUSTERING("CLUSTERING", "集群模式"),

    BROADCASTING("BROADCASTING", "广播模式");

    private final String code;
    private final String name;

    EnumMessageModel(String code, String name) {
        this.code = code;
        this.name = name;
    }


}
