package com.evgeny.kafka.atmostonce.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * ✅ Настоящий at-most-once:
 * - commit ДО обработки
 * - падение ПОСЛЕ commit => сообщение потеряно
 *
 * ⚠️ В модуле не должно быть @KafkaListener, иначе Spring будет делать retries/seeks.
 */
@Slf4j
@Component
public class AtMostOnceManualConsumer implements CommandLineRunner {

    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topic}")
    private String topic;

    @Value("${app.kafka.group-id}")
    private String groupId;

    @Value("${app.kafka.enable-auto-commit:false}")
    private boolean enableAutoCommit;

    @Value("${app.kafka.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Override
    public void run(String... args) {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);              // где Kafka
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);                               // группа
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());   // key->String
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // value->String
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);              // earliest/latest
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(enableAutoCommit)); // false
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");                           // 1 сообщение за poll (чтобы демо было понятнее)

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

            consumer.subscribe(Collections.singletonList(topic)); // подписка на топик

            log.info("🔥 START at-most-once manual consumer topic={} groupId={}", topic, groupId);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500)); // читаем пачку

                for (ConsumerRecord<String, String> record : records) {

                    log.info("📩 RECEIVED partition={}, offset={}, key={}, value={}",
                            record.partition(), record.offset(), record.key(), record.value());

                    // ✅ Сначала коммитим offset (сообщение считается обработанным)
                    consumer.commitSync();
                    log.info("✅ COMMITTED offset={} (before processing)", record.offset());

                    // 💥 Падаем после коммита => сообщение гарантированно потеряется
                    if (record.value() != null && record.value().contains("FAIL")) {
                        log.error("💥 BOOM AFTER COMMIT value={}", record.value());
                        throw new RuntimeException("Simulated crash AFTER commit (at-most-once => lost)");
                    }

                    log.info("✅ PROCESSED value={}", record.value());
                }
            }
        }
    }
}
