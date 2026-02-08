package com.evgeny.kafka.outbox.persistence.repository;

import com.evgeny.kafka.outbox.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
