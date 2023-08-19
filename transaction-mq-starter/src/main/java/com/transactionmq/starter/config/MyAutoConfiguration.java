package com.transactionmq.starter.config;


import com.transactionmq.starter.core.MessageManager;
import com.transactionmq.starter.mq.RocketMQProducer;
import com.transactionmq.starter.support.BaseMessageSender;
import com.transactionmq.starter.support.DefaultMessageMapper;
import com.transactionmq.starter.support.DefaultSqlStatement;
import com.transactionmq.starter.support.DefaultSqlStatementImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(value = {"com.transactionmq.starter.**"})
public class MyAutoConfiguration {

    @Autowired
    public DataSource dataSource;
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplate;


    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${transaction-mq.maxRetryTimes:-1}")
    public Integer maxRetryTimes;

    @Bean
    public MessageManager messageManager(BaseMessageSender messageSender) {
        return new MessageManager(messageSender);
    }

    @Bean
    public DefaultMessageMapper messageMapper(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate, DefaultSqlStatement defaultSqlStatement) {
        return new DefaultMessageMapper(dataSource, jdbcTemplate, defaultSqlStatement);
    }

    @Bean
    public DefaultSqlStatement baseSqlStatement() {
        return new DefaultSqlStatementImpl();
    }

    @Bean
    public BaseMessageSender baseMessageSender(DefaultMessageMapper defaultMessageMapper, RocketMQProducer producer) {
        return new BaseMessageSender(defaultMessageMapper, producer, maxRetryTimes);
    }

    @Bean
    @ConditionalOnProperty(prefix = "transaction-mq", name = "type", havingValue = "rocketMQ")
    public RocketMQProducer rocketMQProducer() {
        return new RocketMQProducer(rocketMQTemplate());
    }

    @Bean
    public RocketMQTemplate rocketMQTemplate() {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        defaultMQProducer.setNamesrvAddr(nameServer);
        defaultMQProducer.setProducerGroup(producerGroup);
        rocketMQTemplate.setProducer(defaultMQProducer);
        return rocketMQTemplate;
    }

}
