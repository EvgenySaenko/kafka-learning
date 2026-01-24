package com.evgeny.kafkajsondemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-объект, который будет сериализоваться в JSON и передаваться через Kafka.
 */
@Data // Генерирует геттеры/сеттеры, toString, equals и hashCode
@AllArgsConstructor
@NoArgsConstructor  // Нужен для десериализации JSON (Kafka требует пустой конструктор)
public class MessageDto {
    private String key;   // Ключ сообщения Kafka (может использоваться для партиционирования)
    private String value; // Значение, которое передаётся (будет сериализовано в JSON)
}
