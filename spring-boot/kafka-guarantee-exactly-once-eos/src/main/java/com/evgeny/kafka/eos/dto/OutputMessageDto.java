package com.evgeny.kafka.eos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutputMessageDto {
    private String messageId;
    private String key;
    private String result; // например "PROCESSED:<value>"
}
