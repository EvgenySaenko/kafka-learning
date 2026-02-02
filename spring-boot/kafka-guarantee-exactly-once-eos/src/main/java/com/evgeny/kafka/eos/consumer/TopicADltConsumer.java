package com.evgeny.kafka.eos.consumer;

import com.evgeny.kafka.eos.dto.InputMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class TopicADltConsumer {

    @KafkaListener(
            topics = "${app.kafka.topic-a}.DLT",
            groupId = "${app.kafka.group-id}-dlt",
            containerFactory = "kafkaListenerContainerFactory" // можно и без, но так явно
    )
    public void listen(InputMessageDto dto) {
        log.error("☠️ A.DLT RECEIVED messageId={}, key={}, value={}",
                dto.getMessageId(), dto.getKey(), dto.getValue());
    }
}
