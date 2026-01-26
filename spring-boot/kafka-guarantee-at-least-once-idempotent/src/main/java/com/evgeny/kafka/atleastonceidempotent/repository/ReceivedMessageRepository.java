package com.evgeny.kafka.atleastonceidempotent.repository;

import com.evgeny.kafka.atleastonceidempotent.entity.ReceivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedMessageRepository extends JpaRepository<ReceivedMessage, Long> {
    boolean existsByMessageId(String messageId);
}
