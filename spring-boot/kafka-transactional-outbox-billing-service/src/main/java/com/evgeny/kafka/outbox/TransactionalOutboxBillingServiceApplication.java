package com.evgeny.kafka.outbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionalOutboxBillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionalOutboxBillingServiceApplication.class, args);
    }
}
