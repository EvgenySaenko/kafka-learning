package com.evgeny.kafka.outbox.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    /**
     * 5 попыток с паузой 2 секунды:
     *  - первая обработка + 4 ретрая = 5 (в зависимости от версии/счётчика)
     *  После исчерпания — публикуем в DLT.
     */
    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // кладём в <topic>.DLT, партиция та же
                (ConsumerRecord<?, ?> record, Exception ex) ->
                        new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        FixedBackOff backOff = new FixedBackOff(2000L, 4L); // 2s, 4 retry (плюс первая попытка)

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // Можно “не ретраить” некоторые исключения (пример):
        // handler.addNotRetryableExceptions(IllegalArgumentException.class);

        return handler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler defaultErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler); // вот тут подключаем “продовый” error handling
        return factory;
    }
}
