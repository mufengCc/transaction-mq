package com.transactionmq.starter.support;

public class DefaultSqlStatementImpl implements DefaultSqlStatement {

    private static final String SQL_INSERT = "insert into local_message_record " +
            "(service, business, model, topic, tags, msg_key, body, max_retry_times, create_time)" +
            " values " +
            "(:service, :business, :model, :topic, :tags, :msgKey, :body, :maxRetryTimes, :createTime)";

    private static final String SQL_SUCCESS = "update local_message_record set " +
            " msg_id = :msgId," +
            " status = :status," +
            " send_success_time = :sendSuccessTime" +
            " where id = :id";

    private static final String SQL_FAILED = "update local_message_record set " +
            " status = :status," +
            " current_retry_times = (current_retry_times + 1)" +
            " where id = :id";

    private static final String SQL_RETRY = "update local_message_record set " +
            " status = :status," +
            " current_retry_times = (current_retry_times + 1)" +
            " where id = :id";

    private static final String SQL_SELECT = "select current_retry_times from local_message_record " +
            " where id = :id";

    private static final String SQL_SELECT_FAILED = "select * from local_message_record " +
            " where status = :status";

    @Override
    public String saveSql() {
        return SQL_INSERT;
    }

    @Override
    public String updateSuccess() {
        return SQL_SUCCESS;
    }

    @Override
    public String updateFailed() {
        return SQL_FAILED;
    }

    @Override
    public String updateRetry() {
        return SQL_RETRY;
    }

    @Override
    public String select() {
        return SQL_SELECT;
    }

    @Override
    public String selectFailed() {
        return SQL_SELECT_FAILED;
    }

}
