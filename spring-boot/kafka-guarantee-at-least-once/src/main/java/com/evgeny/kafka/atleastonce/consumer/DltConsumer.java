package com.evgeny.kafka.atleastonce.consumer;

import com.evgeny.kafka.atleastonce.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DltConsumer {

    @KafkaListener(topics = "${app.kafka.topic}.DLT", groupId = "${app.kafka.group-id}-dlt")
    public void listenDlt(MessageDto dto) {
        log.error("☠️ DLT RECEIVED key={}, value={}", dto.getKey(), dto.getValue()); // сюда попадут после ретраев
    }
}
