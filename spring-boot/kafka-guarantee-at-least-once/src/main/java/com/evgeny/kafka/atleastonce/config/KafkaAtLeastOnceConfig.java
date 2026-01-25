package com.evgeny.kafka.atleastonce.config;

import com.evgeny.kafka.atleastonce.dto.MessageDto;
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
public class KafkaAtLeastOnceConfig {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate; // Тем же template публикуем и в DLT

    @Value("${app.kafka.topic}")
    private String topic; // Основной топик из yml

    @Bean
    public NewTopic mainTopic() {
        return new NewTopic(topic, 3, (short) 1); // Основной топик: 3 партиции (как в твоих демо)
    }

    @Bean
    public NewTopic dltTopic() {
        return new NewTopic(topic + ".DLT", 3, (short) 1); // DLT топик: обычно такое же число партиций
    }

    @Bean
    public DefaultErrorHandler errorHandler() {

        // 1) Recoverer: куда отправлять сообщение, если ретраи исчерпаны
        // По умолчанию Spring делает <topic>.DLT, но мы явно укажем правило и сохраним partition.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()) // тот же partition
        );

        // 2) Backoff + retries:
        // ExponentialBackOffWithMaxRetries(3) => 3 РЕТРАЯ после первой неудачи
        // Итого попыток обработки будет 1 (первая) + 3 (повторы) = 4.
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(500L);  // стартовая пауза 0.5 сек
        backOff.setMultiplier(2.0);        // 0.5s, 1s, 2s
        backOff.setMaxInterval(5_000L);    // максимум 5 сек

        // 3) DefaultErrorHandler:
        // - будет делать retry с backoff
        // - после исчерпания ретраев вызовет recoverer и отправит в DLT
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // 4) (опционально) какие исключения НЕ ретраить, а сразу в DLT
        // Например, ошибки валидации/парсинга.
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }
}
