package com.evgeny.kafkademo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageConsumer {

    @KafkaListener(topics = "demo-topic", groupId = "spring-consumer-group")
    public void listen(ConsumerRecord<String, String> record) {
        log.info("📩 Получено сообщение: topic={}, partition={}, offset={}, key={}, value={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}
