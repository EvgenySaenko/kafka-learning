package com.evgeny.kafka.atleastonceidempotent.config;

import com.evgeny.kafka.atleastonceidempotent.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
@RequiredArgsConstructor
public class KafkaAtLeastOnceIdempotentConfig {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    @Bean
    public NewTopic mainTopic() {
        return new NewTopic(topic, 3, (short) 1);
    }

    @Bean
    public NewTopic dltTopic() {
        return new NewTopic(topic + ".DLT", 3, (short) 1);
    }

    @Bean
    public DefaultErrorHandler errorHandler() {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        // 3 ретрая после первой попытки -> всего 4 попытки
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(500L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5_000L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // Валидацию / явную ошибку формата лучше не ретраить -> сразу DLT
        handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }
}
