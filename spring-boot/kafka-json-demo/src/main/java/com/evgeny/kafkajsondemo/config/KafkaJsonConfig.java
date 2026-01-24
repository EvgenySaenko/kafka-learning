package com.evgeny.kafkajsondemo.config;

import com.evgeny.kafkajsondemo.dto.MessageDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaJsonConfig {

    // ===== 🛠 БИН ДЛЯ ПРОДЮСЕРА =====

    @Bean
    public ProducerFactory<String, MessageDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Адрес брокера
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Сериализатор ключа
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Сериализатор значений в JSON

        return new DefaultKafkaProducerFactory<>(config); // Фабрика продюсера с конфигом
    }

    @Bean
    public KafkaTemplate<String, MessageDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory()); // KafkaTemplate для отправки MessageDto
    }

    // ===== 🛠 БИН ДЛЯ КОНСЮМЕРА =====

    @Bean
    public ConsumerFactory<String, MessageDto> consumerFactory() {
        JsonDeserializer<MessageDto> deserializer = new JsonDeserializer<>(MessageDto.class); // JSON-десериализатор
        deserializer.setRemoveTypeHeaders(false);         // Сохранять типы
        deserializer.addTrustedPackages("*");             // Доверять всем пакетам
        deserializer.setUseTypeMapperForKey(true);        // Использовать типы для ключей

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Адрес брокера
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "json-group");              // Группа консюмеров
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");       // Читать с начала при отсутствии offset
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);   // Десериализатор ключа
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);   // Десериализатор значений

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),    // Десериализатор ключа
                deserializer                 // Десериализатор значения (MessageDto)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // ✅ Подключаем Dead Letter Handler через DefaultErrorHandler
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate()), // 📨 Отправка в DLT
                new FixedBackOff(0L, 0) // 🔁 Без retry
        ));

        return factory;
    }

    // 🔁 DLT-топик для ошибок
    @Bean
    public NewTopic jsonDemoDltTopic() {
        return new NewTopic("json-demo-topic.DLT", 1, (short) 1); // 🔁 DLT-топик для ошибок
    }

}
