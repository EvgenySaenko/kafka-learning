package com.evgeny.kafka.outbox.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Publisher publisher = new Publisher();

    @Getter
    @Setter
    public static class Publisher {
        private boolean enabled;
        private long fixedDelayMs;
        private int batchSize;
        private String topic;
    }
}
