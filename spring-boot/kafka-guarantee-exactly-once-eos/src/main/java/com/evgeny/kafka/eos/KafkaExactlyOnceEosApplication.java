package com.evgeny.kafka.eos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaExactlyOnceEosApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaExactlyOnceEosApplication.class, args);
    }
}

