package com.evgeny.kafka.outbox.outbox;

import com.evgeny.kafka.outbox.config.AppProperties;
import com.evgeny.kafka.outbox.domain.OutboxEventStatus;
import com.evgeny.kafka.outbox.persistence.entity.OutboxEventEntity;
import com.evgeny.kafka.outbox.persistence.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublishService {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AppProperties appProperties;

    @Transactional
    public void publishOnce() {
        var p = appProperties.getPublisher();

        List<OutboxEventEntity> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventStatus.NEW,
                PageRequest.of(0, p.getBatchSize())
        );

        if (events.isEmpty()) {
            return;
        }

        log.info("OutboxPublisher: found {} NEW events, publishing to topic='{}'", events.size(), p.getTopic());

        for (OutboxEventEntity e : events) {
            try {
                kafkaTemplate.send(p.getTopic(), e.getPartitionKey(), e.getPayload()).get();

                e.setStatus(OutboxEventStatus.SENT);
                e.setSentAt(OffsetDateTime.now());
                e.setLastError(null);

                log.info("OutboxPublisher: SENT eventId={}, topic={}, key={}", e.getId(), p.getTopic(), e.getPartitionKey());
            } catch (Exception ex) {
                e.setStatus(OutboxEventStatus.ERROR);
                e.setRetryCount(e.getRetryCount() + 1);
                e.setLastError(shortError(ex));

                log.warn("OutboxPublisher: ERROR eventId={}, retryCount={}, message={}",
                        e.getId(), e.getRetryCount(), e.getLastError(), ex);
            }
        }
    }

    private String shortError(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) return ex.getClass().getSimpleName();
        return msg.length() > 1000 ? msg.substring(0, 1000) : msg;
    }
}
