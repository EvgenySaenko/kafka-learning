package com.evgeny.kafkademo.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String key, String value) {
        log.info("📤 Отправка сообщения в Kafka: topic={}, key={}, value={}", topic, key, value);
        kafkaTemplate.send(topic, key, value);
    }
}
