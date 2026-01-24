package com.evgeny.kafka.atmostonce.controller;


import com.evgeny.kafka.atmostonce.dto.MessageDto;
import com.evgeny.kafka.atmostonce.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ProduceController {

    private final KafkaProducerService producer;

    @PostMapping // POST /api/messages
    public ResponseEntity<String> send(@RequestBody MessageDto dto) { // принимаем JSON
        producer.send(dto.getKey(), dto.getValue()); // отправляем в Kafka
        return ResponseEntity.ok("OK"); // простой ответ
    }
}
