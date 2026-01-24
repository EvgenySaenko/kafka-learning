package com.evgeny.kafkajsondemo.controller;

import com.evgeny.kafkajsondemo.dto.MessageDto;
import com.evgeny.kafkajsondemo.producer.KafkaJsonProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер, принимающий сообщения от клиента и отправляющий их в Kafka.
 */
@RestController
@RequestMapping("/api/json")
@RequiredArgsConstructor
public class MessageJsonController {

    private final KafkaJsonProducer producer;

    /**
     * Приём POST-запроса и отправка сообщения в Kafka.
     */
    @PostMapping
    public ResponseEntity<String> sendJson(@RequestBody MessageDto dto) {
        producer.sendMessage("json-demo-topic", dto);
        return ResponseEntity.ok("✅ JSON-сообщение отправлено в Kafka");
    }
}
