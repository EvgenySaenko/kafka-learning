package com.evgeny.kafkademo.controller;

import com.evgeny.kafkademo.dto.MessageDto;
import com.evgeny.kafkademo.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaMessageProducer producer;

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto message) {
        producer.sendMessage("demo-topic", message.getKey(), message.getValue());
        return ResponseEntity.ok("✅ Сообщение отправлено");
    }
}
