package com.evgeny.kafka.outbox.integration.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private UUID eventId;
    private UUID orderId;
    private BigDecimal amount;
}
