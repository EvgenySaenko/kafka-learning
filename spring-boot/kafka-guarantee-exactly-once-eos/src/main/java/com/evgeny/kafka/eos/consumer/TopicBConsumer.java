package com.evgeny.kafka.eos.consumer;

import com.evgeny.kafka.eos.dto.OutputMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TopicBConsumer {

    @KafkaListener(topics = "${app.kafka.topic-b}", groupId = "${app.kafka.group-id}-b")
    public void listen(OutputMessageDto dto) {
        log.info("✅ B RECEIVED messageId={}, key={}, result={}", dto.getMessageId(), dto.getKey(), dto.getResult());
    }
}
