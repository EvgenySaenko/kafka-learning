package com.evgeny.kafka.eos.consumer;

import com.evgeny.kafka.eos.dto.InputMessageDto;
import com.evgeny.kafka.eos.dto.OutputMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EosProcessorConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic-b}")
    private String topicB;

    @KafkaListener(
            topics = "${app.kafka.topic-a}",
            groupId = "${app.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            InputMessageDto dto,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("📩 A RECEIVED partition={}, offset={}, messageId={}, key={}, value={}",
                partition, offset, dto.getMessageId(), dto.getKey(), dto.getValue());

        // ✅ Валидация (проверим, что IllegalArgumentException улетит сразу в DLT)
        if (dto.getMessageId() == null || dto.getMessageId().isBlank()) {
            throw new IllegalArgumentException("messageId is required (EOS demo validation)");
        }

        // 1) FAIL_BEFORE_SEND: упали до отправки -> в B ничего не появится, будет retry, потом DLT
        if ("FAIL_BEFORE_SEND".equalsIgnoreCase(dto.getValue())) {
            throw new RuntimeException("Simulated crash BEFORE send");
        }

        // 2) Отправляем в B (это часть Kafka-транзакции)
        OutputMessageDto out = OutputMessageDto.builder()
                .messageId(dto.getMessageId())
                .key(dto.getKey())
                .result("PROCESSED:" + dto.getValue())
                .build();

        kafkaTemplate.send(topicB, dto.getKey(), out);
        log.info("➡️ SENT to B topic={}, messageId={}", topicB, dto.getMessageId());

        // 3) FAIL_AFTER_SEND: отправили, но упали ДО коммита транзакции
        // Из-за транзакции запись в B будет ОТМЕНЕНА (в UI её не увидишь), retry -> потом DLT
        if ("FAIL_AFTER_SEND".equalsIgnoreCase(dto.getValue())) {
            throw new RuntimeException("Simulated crash AFTER send (TX rollback, nothing in B)");
        }

        // 4) SLOW_AFTER_SEND: удобно руками успеть "убить" приложение во время транзакции
        if ("SLOW_AFTER_SEND".equalsIgnoreCase(dto.getValue())) {
            try {
                log.info("SLOW_AFTER_SEND - sleep");
                Thread.sleep(15_000L);
            } catch (InterruptedException ignored) {}
        }

        // ВАЖНО: никаких ack.acknowledge() тут нет.
        // Offset + send(B) коммитятся вместе через KafkaTransactionManager.
        log.info("✅ DONE (TX will commit send(B) + offsets atomically) messageId={}", dto.getMessageId());
    }
}
