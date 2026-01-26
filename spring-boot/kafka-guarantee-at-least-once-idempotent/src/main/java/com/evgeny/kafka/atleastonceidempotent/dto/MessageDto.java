package com.evgeny.kafka.atleastonceidempotent.dto;

import lombok.Data;

@Data
public class MessageDto {
    private String messageId; // UUID / уникальный id
    private String key;       // Kafka key (влияет на partition)
    private String value;     // payload
}
