package com.evgeny.kafka.atleastonceidempotent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaAtLeastOnceIdempotentApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaAtLeastOnceIdempotentApplication.class, args);
    }
}