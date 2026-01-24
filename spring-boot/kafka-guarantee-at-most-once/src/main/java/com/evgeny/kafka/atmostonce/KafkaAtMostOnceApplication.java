package com.evgeny.kafka.atmostonce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaAtMostOnceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaAtMostOnceApplication.class, args);
    }
}