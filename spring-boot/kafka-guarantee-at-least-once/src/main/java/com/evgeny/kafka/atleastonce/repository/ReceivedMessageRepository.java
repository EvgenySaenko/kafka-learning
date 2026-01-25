package com.evgeny.kafka.atleastonce.repository;

import com.evgeny.kafka.atleastonce.entity.ReceivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedMessageRepository extends JpaRepository<ReceivedMessage, Long> {
}
