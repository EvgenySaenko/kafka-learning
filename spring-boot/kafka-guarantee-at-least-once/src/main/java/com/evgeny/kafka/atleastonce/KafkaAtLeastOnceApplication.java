package com.evgeny.kafka.atleastonce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaAtLeastOnceApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaAtLeastOnceApplication.class, args);
    }
}
