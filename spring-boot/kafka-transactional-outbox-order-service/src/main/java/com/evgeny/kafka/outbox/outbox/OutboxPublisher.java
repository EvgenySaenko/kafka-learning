package com.evgeny.kafka.outbox.outbox;

import com.evgeny.kafka.outbox.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxPublishService outboxPublishService;
    private final AppProperties appProperties;

    @Scheduled(fixedDelayString = "${app.publisher.fixed-delay-ms}")
    public void publishBatch() {
        if (!appProperties.getPublisher().isEnabled()) {
            return;
        }
        outboxPublishService.publishOnce();
    }
}
