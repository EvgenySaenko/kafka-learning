package com.evgeny.kafkajsondemo.consumer;

import com.evgeny.kafkajsondemo.dto.MessageDto;
import com.evgeny.kafkajsondemo.service.MessageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka-консьюмер, слушающий сообщения в формате JSON и автоматически преобразующий их в DTO.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaJsonConsumer {

    private final MessageProcessingService messageProcessingService;

    /**
     * Обработка сообщений из Kafka. Kafka сам десериализует JSON в MessageDto.
     */
    @KafkaListener(
            topics = "json-demo-topic",
            groupId = "json-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(MessageDto message) {
        log.info("📩 Получено JSON-сообщение: key={}, value={}", message.getKey(), message.getValue());

        try {
            // 🛠️ Передаём в сервис обработки
            messageProcessingService.processMessage(message);

        } catch (Exception e) {
            // ⚠️ Если возникла ошибка — логируем её (или отправляем в DLT позже)
            log.error("❌ Ошибка при обработке сообщения key={}: {}", message.getKey(), e.getMessage(), e);
        }
    }
}
