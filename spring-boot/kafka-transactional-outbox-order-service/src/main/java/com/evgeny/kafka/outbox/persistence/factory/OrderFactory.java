package com.evgeny.kafka.outbox.persistence.factory;

import com.evgeny.kafka.outbox.domain.OrderStatus;
import com.evgeny.kafka.outbox.persistence.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class OrderFactory {

    public OrderEntity newOrder(BigDecimal amount, OffsetDateTime now) {
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID());
        order.setAmount(amount);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(now);
        return order;
    }
}
