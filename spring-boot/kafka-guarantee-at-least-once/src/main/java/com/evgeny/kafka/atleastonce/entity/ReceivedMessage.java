package com.evgeny.kafka.atleastonce.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 👈 PK

    @Column(name = "message_key")
    private String messageKey; // 👈 не "key", чтобы не конфликтовать с SQL

    @Column(name = "message_value")
    private String messageValue; // 👈 не "value", чтобы H2 не ругался

    private LocalDateTime receivedAt; // 👈 когда сохранили
}
