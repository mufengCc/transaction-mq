package com.transactionmq.starter.support;

public interface DefaultSqlStatement {

    String saveSql();

    String updateSuccess();

    String updateFailed();

    String updateRetry();

    String select();

    String selectFailed();


}
