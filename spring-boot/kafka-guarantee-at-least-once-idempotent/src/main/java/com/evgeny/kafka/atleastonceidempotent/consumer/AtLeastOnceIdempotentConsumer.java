package com.evgeny.kafka.atleastonceidempotent.consumer;

import com.evgeny.kafka.atleastonceidempotent.dto.MessageDto;
import com.evgeny.kafka.atleastonceidempotent.service.MessageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtLeastOnceIdempotentConsumer {

    private final MessageProcessingService service;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${app.kafka.group-id}")
    public void listen(MessageDto dto, Acknowledgment ack) {

        log.info("📩 RECEIVED messageId={}, key={}, value={}", dto.getMessageId(), dto.getKey(), dto.getValue());

        try {
            service.process(dto);
            ack.acknowledge();
            log.info("✅ ACKED offset (after idempotent processing)");
        } catch (Exception e) {
            log.error("💥 PROCESS FAILED (will be handled by error handler): {}", e.toString());
            throw e; // КЛЮЧЕВО: чтобы DefaultErrorHandler отправил в DLT
        }
    }
}
