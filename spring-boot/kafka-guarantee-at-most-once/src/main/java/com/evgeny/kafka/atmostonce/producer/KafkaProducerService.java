package com.evgeny.kafka.atmostonce.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate; // Spring Kafka клиент для отправки

    @Value("${app.kafka.topic}")
    private String topic; // топик берём из application.yml

    public void send(String key, String value) {
        log.info("📤 SEND -> topic={}, key={}, value={}", topic, key, value); // логируем отправку
        kafkaTemplate.send(topic, key, value); // отправляем в Kafka
    }
}