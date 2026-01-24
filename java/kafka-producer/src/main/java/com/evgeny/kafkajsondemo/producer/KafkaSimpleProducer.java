package com.evgeny.kafkajsondemo.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaSimpleProducer implements AutoCloseable {

    private final KafkaProducer<String, String> producer;

    public KafkaSimpleProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    /**
     * Отправляет сообщение без ключа (Kafka сама выберет партицию).
     */
    public void send(String topic, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record);
        System.out.printf("✔ Sent to topic=%s value=%s%n", topic, message);
    }

    /**
     * Отправляет сообщение с ключом — ключ влияет на выбор партиции.
     */
    public void send(String topic, String key, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
        producer.send(record);
        System.out.printf("✔ Sent to topic=%s key=%s value=%s%n", topic, key, message);
    }

    /**
     * Отправляет и ждёт подтверждения от Kafka (блокирует поток).
     * Удобно, если нужна точная гарантия доставки.
     */
    public void sendWaitResponse(String topic, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        try {
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get(); // блокирует поток
            System.out.printf("✔ [sync] topic=%s, partition=%d, offset=%d%n",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (Exception e) {
            System.err.println("❌ Ошибка при синхронной отправке: " + e.getMessage());
        }
    }

    /**
     * Асинхронная отправка с callback-ом (не блокирует поток).
     * Удобно, если ты хочешь продолжить выполнение, а Kafka сама вызовет callback.
     */
    public void sendAsyncWithCallback(String topic, String key, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.printf("✔ [async] topic=%s, partition=%d, offset=%d key=%s value=%s%n",
                        metadata.topic(), metadata.partition(), metadata.offset(), key, message);
            } else {
                System.err.printf("❌ Ошибка при асинхронной отправке key=%s: %s%n", key, exception.getMessage());
            }
        });
    }

    @Override
    public void close() {
        producer.close();
    }
}
