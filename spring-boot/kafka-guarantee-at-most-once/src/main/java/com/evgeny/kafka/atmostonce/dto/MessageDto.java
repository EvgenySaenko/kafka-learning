package com.evgeny.kafka.atmostonce.dto;


import lombok.Data;

@Data // Lombok: геттеры/сеттеры/equals/hashCode/toString
public class MessageDto {

    private String key;   // ключ Kafka (влияет на partition)
    private String value; // полезная нагрузка
}
