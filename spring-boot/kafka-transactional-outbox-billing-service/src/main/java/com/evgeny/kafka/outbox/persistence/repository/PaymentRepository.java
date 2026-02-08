package com.evgeny.kafka.outbox.persistence.repository;

import com.evgeny.kafka.outbox.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
