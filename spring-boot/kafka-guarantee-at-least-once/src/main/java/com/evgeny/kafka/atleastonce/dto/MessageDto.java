package com.evgeny.kafka.atleastonce.dto;

import lombok.Data;

@Data
public class MessageDto {
    private String key;    // 👈 Kafka key
    private String value;  // 👈 payload
}