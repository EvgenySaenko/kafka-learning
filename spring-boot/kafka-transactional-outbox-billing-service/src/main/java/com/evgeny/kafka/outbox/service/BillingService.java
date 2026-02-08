package com.evgeny.kafka.outbox.service;

import com.evgeny.kafka.outbox.integration.event.OrderCreatedEvent;
import com.evgeny.kafka.outbox.persistence.entity.PaymentEntity;
import com.evgeny.kafka.outbox.persistence.entity.ProcessedEventEntity;
import com.evgeny.kafka.outbox.persistence.repository.PaymentRepository;
import com.evgeny.kafka.outbox.persistence.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final ProcessedEventRepository processedEventRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void handle(OrderCreatedEvent event) {
        UUID eventId = event.getEventId();
        if (true) throw new RuntimeException("boom");

        if (processedEventRepository.existsById(eventId)) {
            log.info("Billing: duplicate eventId={}, skip", eventId);
            return;
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setId(UUID.randomUUID());
        payment.setEventId(eventId);
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getAmount());
        payment.setCreatedAt(OffsetDateTime.now());

        paymentRepository.save(payment);

        ProcessedEventEntity processed = new ProcessedEventEntity();
        processed.setEventId(eventId);
        processed.setProcessedAt(OffsetDateTime.now());

        processedEventRepository.save(processed);

        log.info("Billing: saved paymentId={}, orderId={}, eventId={}",
                payment.getId(), payment.getOrderId(), eventId);
    }
}
