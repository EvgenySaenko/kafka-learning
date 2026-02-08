package com.evgeny.kafka.outbox.service;

import com.evgeny.kafka.outbox.integration.event.OrderCreatedEvent;
import com.evgeny.kafka.outbox.persistence.entity.OrderEntity;
import com.evgeny.kafka.outbox.persistence.factory.OrderFactory;
import com.evgeny.kafka.outbox.persistence.factory.OutboxEventFactory;
import com.evgeny.kafka.outbox.persistence.repository.OrderRepository;
import com.evgeny.kafka.outbox.persistence.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;

    private final OrderFactory orderFactory;
    private final OutboxEventFactory outboxEventFactory;

    @Transactional
    public UUID createOrder(BigDecimal amount) {
        OffsetDateTime now = OffsetDateTime.now();

        OrderEntity order = orderRepository.save(orderFactory.newOrder(amount, now));

        outboxEventRepository.save(outboxEventFactory.orderCreated(order.getId(), amount, now));

        return order.getId();
    }
}
