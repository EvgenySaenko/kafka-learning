package com.evgeny.kafka.atleastonceidempotent.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "received_message",
        indexes = {
                @Index(name = "ix_received_message_message_id", columnList = "message_id", unique = true)
        }
)
public class ReceivedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ключ дедупликации
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "message_key")
    private String messageKey;

    @Column(name = "message_value")
    private String messageValue;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
}
