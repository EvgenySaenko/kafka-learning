package com.evgeny.kafka.outbox.integration.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private UUID eventId;
    private UUID orderId;
    private BigDecimal amount;
}
