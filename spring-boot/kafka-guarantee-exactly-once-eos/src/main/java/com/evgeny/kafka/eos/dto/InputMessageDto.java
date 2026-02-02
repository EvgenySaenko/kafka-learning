package com.evgeny.kafka.eos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputMessageDto {
    private String messageId;

    @NotBlank
    private String key;

    @NotBlank
    private String value;
}
