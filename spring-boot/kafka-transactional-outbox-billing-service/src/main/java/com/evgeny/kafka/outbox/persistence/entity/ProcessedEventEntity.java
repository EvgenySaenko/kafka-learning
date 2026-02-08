package com.evgeny.kafka.outbox.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "processed_event", schema = "outbox_billing")
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id", columnDefinition = "uuid")
    private UUID eventId;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt;
}
