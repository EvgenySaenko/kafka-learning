package com.evgeny.kafka.atleastonceidempotent.controller;

import com.evgeny.kafka.atleastonceidempotent.dto.MessageDto;
import com.evgeny.kafka.atleastonceidempotent.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ProduceController {

    private final KafkaProducerService producer;

    @PostMapping
    public ResponseEntity<String> send(@RequestBody MessageDto dto) {

        // удобно для Postman: можно не передавать messageId, а мы сгенерим
        if (dto.getMessageId() == null || dto.getMessageId().isBlank()) {
            dto.setMessageId(UUID.randomUUID().toString());
        }

        producer.send(dto);
        return ResponseEntity.ok(dto.getMessageId());
    }
}
