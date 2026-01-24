package com.evgeny.kafkajsondemo.repository;

import com.evgeny.kafkajsondemo.entity.ReceivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivedMessageRepository extends JpaRepository<ReceivedMessage, Long> {
}
