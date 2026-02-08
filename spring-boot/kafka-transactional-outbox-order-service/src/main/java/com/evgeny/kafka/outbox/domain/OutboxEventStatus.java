package com.evgeny.kafka.outbox.domain;

public enum OutboxEventStatus {
    NEW,
    SENT,
    ERROR
}
