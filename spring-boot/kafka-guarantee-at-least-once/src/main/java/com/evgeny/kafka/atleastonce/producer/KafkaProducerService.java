package com.evgeny.kafka.atleastonce.producer;

import com.evgeny.kafka.atleastonce.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate; // 👈 отправляем DTO

    @Value("${app.kafka.topic}")
    private String topic; // 👈 топик из yml

    public void send(MessageDto dto) {
        log.info("📤 SEND -> topic={}, key={}, value={}", topic, dto.getKey(), dto.getValue());
        kafkaTemplate.send(topic, dto.getKey(), dto); // 👈 key влияет на partition
    }
}
