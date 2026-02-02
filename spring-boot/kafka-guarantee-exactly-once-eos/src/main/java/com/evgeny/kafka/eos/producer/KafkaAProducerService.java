package com.evgeny.kafka.eos.producer;

import com.evgeny.kafka.eos.dto.InputMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaAProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic-a}")
    private String topicA;

    public void send(InputMessageDto dto) {
        log.info("📤 SEND -> topicA={}, messageId={}, key={}, value={}",
                topicA, dto.getMessageId(), dto.getKey(), dto.getValue());

        kafkaTemplate.executeInTransaction(ops -> {
            ops.send(topicA, dto.getKey(), dto);
            return true;
        });
    }
}
