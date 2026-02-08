package com.evgeny.kafka.outbox.persistence.entity;

import com.evgeny.kafka.outbox.domain.AggregateType;
import com.evgeny.kafka.outbox.domain.OrderEventType;
import com.evgeny.kafka.outbox.domain.OutboxEventStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = "outbox_event", schema = "outbox_order")
public class OutboxEventEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregate_type", nullable = false, length = 64)
    private AggregateType aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 128)
    private OrderEventType eventType;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OutboxEventStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "partition_key")
    private String partitionKey;
}

