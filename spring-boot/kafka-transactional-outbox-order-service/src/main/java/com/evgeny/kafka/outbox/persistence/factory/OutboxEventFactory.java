package com.evgeny.kafka.outbox.persistence.factory;

import com.evgeny.kafka.outbox.domain.AggregateType;
import com.evgeny.kafka.outbox.domain.OrderEventType;
import com.evgeny.kafka.outbox.domain.OutboxEventStatus;
import com.evgeny.kafka.outbox.integration.event.OrderCreatedEvent;
import com.evgeny.kafka.outbox.persistence.entity.OutboxEventEntity;
import com.evgeny.kafka.outbox.support.PayloadSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {

    private final PayloadSerializer payloadSerializer;

    public OutboxEventEntity orderCreated(UUID orderId, BigDecimal amount, OffsetDateTime now) {
        UUID eventId = UUID.randomUUID(); // ✅ eventId генерим в приложении

        OrderCreatedEvent payloadObj = new OrderCreatedEvent(eventId, orderId, amount);
        String payloadJson = payloadSerializer.toJson(payloadObj);

        OutboxEventEntity outboxEvent = new OutboxEventEntity();
        outboxEvent.setId(eventId);
        outboxEvent.setAggregateType(AggregateType.ORDER);
        outboxEvent.setAggregateId(orderId);
        outboxEvent.setEventType(OrderEventType.ORDER_CREATED);

        outboxEvent.setPayload(payloadJson);

        outboxEvent.setStatus(OutboxEventStatus.NEW);
        outboxEvent.setCreatedAt(now);
        outboxEvent.setRetryCount(0);
        outboxEvent.setPartitionKey(orderId.toString());

        return outboxEvent;
    }
}

