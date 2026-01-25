package com.evgeny.kafka.atleastonce.controller;

import com.evgeny.kafka.atleastonce.dto.MessageDto;
import com.evgeny.kafka.atleastonce.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ProduceController {

    private final KafkaProducerService producer;

    @PostMapping
    public ResponseEntity<String> send(@RequestBody MessageDto dto) {
        producer.send(dto);
        return ResponseEntity.ok("OK");
    }
}
