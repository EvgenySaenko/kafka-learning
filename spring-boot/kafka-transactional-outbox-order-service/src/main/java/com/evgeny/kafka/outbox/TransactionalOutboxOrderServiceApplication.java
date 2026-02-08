package com.evgeny.kafka.outbox;

import com.evgeny.kafka.outbox.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
@SpringBootApplication
public class TransactionalOutboxOrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionalOutboxOrderServiceApplication.class, args);
    }
}

