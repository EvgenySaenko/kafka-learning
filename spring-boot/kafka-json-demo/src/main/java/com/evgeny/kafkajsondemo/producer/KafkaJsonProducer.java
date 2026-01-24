package com.evgeny.kafkajsondemo.producer;

import com.evgeny.kafkajsondemo.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Сервис, отправляющий сообщения в Kafka в формате JSON.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaJsonProducer {

    // KafkaTemplate с поддержкой отправки объектов MessageDto (автоматически сериализуется в JSON)
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    /**
     * Отправка сообщения в Kafka по указанной теме.
     */
    public void sendMessage(String topic, MessageDto dto) {
        log.info("📤 Отправка JSON-сообщения в Kafka: topic={}, key={}, value={}",
                topic, dto.getKey(), dto.getValue());

        kafkaTemplate.send(topic, dto.getKey(), dto); // Отправка сообщения в Kafka
    }
}
