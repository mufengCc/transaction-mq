package com.transactionmq.test.example.service;

import com.transactionmq.starter.entity.LocalMessageRecordEntity;
import com.transactionmq.starter.support.DefaultMessageMapper;
import com.transactionmq.starter.support.DefaultSqlStatement;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

public class MyBaseMapperDefault extends DefaultMessageMapper {


    public MyBaseMapperDefault(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate, DefaultSqlStatement defaultSqlStatement) {
        super(dataSource, jdbcTemplate, defaultSqlStatement);
    }

    @Override
    public int save(LocalMessageRecordEntity entity) {
        System.out.println("--=====================");
        System.out.println("--=====================");
        System.out.println("--=====================");
        System.out.println("--=====================");
        System.out.println("--=====================");
        System.out.println("--=====================");
        System.out.println("--=====================");
        System.out.println("--=====================");
        return super.save(entity);
    }


}
