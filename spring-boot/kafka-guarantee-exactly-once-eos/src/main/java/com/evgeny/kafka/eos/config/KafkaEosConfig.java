package com.evgeny.kafka.eos.config;

import com.evgeny.kafka.eos.dto.InputMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
@RequiredArgsConstructor
public class KafkaEosConfig {

    @Value("${app.kafka.topic-a}")
    private String topicA;

    @Value("${app.kafka.topic-b}")
    private String topicB;

    // ===== Topics =====

    @Bean
    public NewTopic topicA() {
        return new NewTopic(topicA, 3, (short) 1);
    }

    @Bean
    public NewTopic topicB() {
        return new NewTopic(topicB, 3, (short) 1);
    }

    @Bean
    public NewTopic topicADlt() {
        return new NewTopic(topicA + ".DLT", 3, (short) 1);
    }

    // ===== TX Manager =====
    // КЛЮЧ: этот TM привязывает send(B) + commit offsets в одну Kafka-транзакцию
    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(ProducerFactory<String, Object> pf) {
        return new KafkaTransactionManager<>(pf);
    }

    // ===== EOS Listener Container Factory =====
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InputMessageDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, InputMessageDto> consumerFactory,
            KafkaTransactionManager<String, Object> kafkaTransactionManager,
            AfterRollbackProcessor<String, InputMessageDto> afterRollbackProcessor
    ) {
        ConcurrentKafkaListenerContainerFactory<String, InputMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // ✅ Включаем транзакции для listener'а (offsets и send(B) в одной TX)
        factory.getContainerProperties().setTransactionManager(kafkaTransactionManager);

        // ✅ Современный EOS режим (Kafka 2.5+)
        factory.getContainerProperties().setEosMode(ContainerProperties.EOSMode.V2);

        // ✅ ВАЖНО: для транзакционного listener'а ретраи/DLT должны происходить ПОСЛЕ rollback
        factory.setAfterRollbackProcessor(afterRollbackProcessor);

        return factory;
    }

    // ===== AfterRollbackProcessor: retries -> DLT (после отката транзакции) =====
    @Bean
    public AfterRollbackProcessor<String, InputMessageDto> afterRollbackProcessor(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(500L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5_000L);

        DefaultAfterRollbackProcessor<String, InputMessageDto> processor =
                new DefaultAfterRollbackProcessor<>(recoverer, backOff);

        // ✅ КЛЮЧЕВО: после отправки в DLT — коммитим offset, чтобы не зациклиться
        processor.setCommitRecovered(true);

        // ✅ “не ретраим” для валидации (по желанию можно добавить ещё классы)
        processor.addNotRetryableExceptions(
                IllegalArgumentException.class,
                org.springframework.kafka.support.serializer.DeserializationException.class
        );

        return processor;
    }
}
