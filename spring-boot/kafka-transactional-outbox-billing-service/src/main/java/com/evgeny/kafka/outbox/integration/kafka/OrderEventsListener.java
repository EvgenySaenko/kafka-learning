package com.evgeny.kafka.outbox.integration.kafka;

import com.evgeny.kafka.outbox.integration.event.OrderCreatedEvent;
import com.evgeny.kafka.outbox.service.BillingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderEventsListener {

    private final ObjectMapper objectMapper;
    private final BillingService billingService;

    @KafkaListener(topics = "orders.events", groupId = "billing-service")
    public void onMessage(String payload, Acknowledgment ack)  {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);

            billingService.handle(event); // внутри @Transactional

            ack.acknowledge(); // ✅ коммит offset только после успеха
        } catch (Exception ex) {
            log.error("Failed to process message: {}", payload, ex);
            throw new RuntimeException(ex); // ✅ важно: чтобы сработал DefaultErrorHandler и начал делать retry
            // ❗ offset не коммитим — Kafka доставит сообщение снова
        }
    }

}
