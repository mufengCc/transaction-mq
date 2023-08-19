package com.transactionmq.starter.support;

import com.transactionmq.starter.config.MyAutoConfiguration;
import com.transactionmq.starter.entity.LocalMessageRecordEntity;
import com.transactionmq.starter.enums.EnumMessageStatus;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.util.Objects;

public class DefaultMessageMapper extends MyAutoConfiguration {

    private final DataSource dataSource;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private DefaultSqlStatement defaultSqlStatement;

    public DefaultMessageMapper(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate, DefaultSqlStatement defaultSqlStatement) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.defaultSqlStatement = defaultSqlStatement;
    }

    public void sendDefaultSqlStatement(DefaultSqlStatement defaultSqlStatement) {
        this.defaultSqlStatement = defaultSqlStatement;
    }

    public int save(LocalMessageRecordEntity entity) {
        String sql = defaultSqlStatement.saveSql();
        SqlParameterSource ps = new BeanPropertySqlParameterSource(entity);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, ps, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }


    public void updateSuccess(LocalMessageRecordEntity entity) {
        String sql = defaultSqlStatement.updateSuccess();
        SqlParameterSource ps = new BeanPropertySqlParameterSource(entity);
        jdbcTemplate.update(sql, ps);
    }

    public void updateFailed(LocalMessageRecordEntity entity) {

        if (entity.getMaxRetryTimes() == -1 || select(entity) < entity.getMaxRetryTimes()) {
            updateRetry(entity);
            return;
        }

        entity.setStatus(EnumMessageStatus.FAILED.getCode());
        String sql = defaultSqlStatement.updateFailed();
        SqlParameterSource ps = new BeanPropertySqlParameterSource(entity);
        jdbcTemplate.update(sql, ps);
    }

    public void updateRetry(LocalMessageRecordEntity entity) {
        entity.setStatus(EnumMessageStatus.RETRYING.getCode());
        String sql = defaultSqlStatement.updateRetry();
        SqlParameterSource ps = new BeanPropertySqlParameterSource(entity);
        jdbcTemplate.update(sql, ps);
    }

    public Integer select(LocalMessageRecordEntity entity) {
        String sql = defaultSqlStatement.select();
        SqlParameterSource ps = new BeanPropertySqlParameterSource(entity);
        Integer currentRetryTimeS = jdbcTemplate.queryForObject(sql, ps, Integer.class);
        return currentRetryTimeS == null ? 0 : currentRetryTimeS;
    }

}
