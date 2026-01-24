package com.evgeny.kafkademo.dto;

import lombok.Data;

@Data
public class MessageDto {
    private String key;
    private String value;
}
