package com.evgeny.kafka.outbox.persistence.repository;

import com.evgeny.kafka.outbox.domain.OutboxEventStatus;
import com.evgeny.kafka.outbox.persistence.entity.OutboxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(OutboxEventStatus status, Pageable pageable);
}
