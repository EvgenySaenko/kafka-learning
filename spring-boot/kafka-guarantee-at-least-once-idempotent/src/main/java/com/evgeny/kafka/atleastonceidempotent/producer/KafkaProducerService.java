package com.evgeny.kafka.atleastonceidempotent.producer;

import com.evgeny.kafka.atleastonceidempotent.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void send(MessageDto dto) {
        log.info("📤 SEND -> topic={}, messageId={}, key={}, value={}",
                topic, dto.getMessageId(), dto.getKey(), dto.getValue());

        kafkaTemplate.send(topic, dto.getKey(), dto);
    }
}
