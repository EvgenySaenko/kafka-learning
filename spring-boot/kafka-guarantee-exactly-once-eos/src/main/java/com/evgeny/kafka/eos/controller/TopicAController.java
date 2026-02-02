package com.evgeny.kafka.eos.controller;

import com.evgeny.kafka.eos.dto.InputMessageDto;
import com.evgeny.kafka.eos.producer.KafkaAProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/a")
@RequiredArgsConstructor
public class TopicAController {

    private final KafkaAProducerService producer;

    @PostMapping
    public ResponseEntity<String> send(@RequestBody @Valid InputMessageDto dto) {
        producer.send(dto);
        return ResponseEntity.ok("OK");
    }
}
