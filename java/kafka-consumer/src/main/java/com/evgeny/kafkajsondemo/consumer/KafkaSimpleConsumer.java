package com.evgeny.kafkajsondemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

//        Kafka поддерживает два способа управления offset:
//        ✅ Авто-коммит — offset сохраняются автоматически (по таймеру)
//        ✅ Ручной коммит — ты сам вызываешь commitSync() или commitAsync() в нужный момент
//        ⚠️ Если ты не коммитишь, Kafka считает, что ты не прочитал сообщения, и может прислать их снова (что в некоторых случаях нормально — например, при сбоях).
//        ✅ настройки по умолчанию в Kafka:
//        enable.auto.commit=true — Kafka автоматически коммитит offset после чтения сообщений.
//        auto.commit.interval.ms=5000 — делает это каждые 5 секунд.
public class KafkaSimpleConsumer implements AutoCloseable {

    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(true); // 🔍 Управление флагом завершения

    public KafkaSimpleConsumer(String bootstrapServers, String topic, String groupId, String offsetReset, String isSavingOffsetAuto) {
        Properties props = new Properties();

        // Где находится Kafka-брокер
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Десериализация ключа и значения (по умолчанию строки)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Уникальный ID группы — сообщения будут распределяться между её участниками
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // earliest — если offset-а нет в Kafka, читать с самого начала
        // latest — читать только новые, поступившие после запуска
        // none — выбросить ошибку, если offset не найден
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);

        // ❌ Не сохраняем offset автоматически
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, isSavingOffsetAuto);

        this.consumer = new KafkaConsumer<>(props);

        // 🔍 Подписываемся на один топик
        this.consumer.subscribe(Collections.singletonList(topic));

        // 🧼 Авто-закрытие при завершении JVM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n👋 Получен сигнал на завершение (ShutdownHook)");
            stop(); // безопасно прерываем poll()
            close(); // корректно закрываем consumer
        }));
    }

    // ✅ Безопасное завершение poll (Kafka рекомендует использовать именно wakeup)
    public void stop() {
        isRunning.set(false);
        consumer.wakeup(); // прерывает блокирующий poll()
    }

    // 🔁 Главный метод чтения сообщений
    public void pollMessages() {
        System.out.println("👂 Слушаем Kafka...");

        try {
            while (isRunning.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("📨 Получено: topic=%s, partition=%d, offset=%d, key=%s, value=%s%n",
                            record.topic(), record.partition(), record.offset(),
                            record.key(), record.value());
                }

                // ✅ Ручной коммит offset'ов
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            // 🧼 Ожидаемое исключение при завершении (consumer.wakeup())
            if (isRunning.get()) {
                System.err.println("❌ WakeupException во время работы");
                throw e;
            } else {
                System.out.println("📴 Завершение по wakeup()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        System.out.println("🛑 Закрытие KafkaConsumer");
        consumer.close();
    }
}
