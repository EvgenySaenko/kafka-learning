package com.evgeny.kafkajsondemo;

import org.apache.kafka.clients.producer.*; // Основной API Kafka Producer
import org.apache.kafka.common.serialization.StringSerializer; // Сериализатор строк

import java.util.Properties;

public class SimpleKafkaProducer {
    public static void main(String[] args) {
        // 🔹 Имя топика, в который будем отправлять
        String topic = "demo-topic";

        // 🔹 Адрес Kafka-брокера (Docker работает на localhost)
        String bootstrapServers = "localhost:9092";

        // 🔹 Конфигурация продюсера
        Properties props = new Properties();

        // 📌 Указываем Kafka-брокер(ы), к которому подключаться
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 📌 Сериализатор ключа (key) — преобразует Java-строку в байты
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 📌 Сериализатор значения (value) — тоже строка → байты
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 🔹 Создаём продюсер
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 🔹 Отправим несколько сообщений в топик
        for (int i = 1; i <= 5; i++) {
            String key = "key-" + i;
            String value = "Hello from Java Producer, message " + i;

            // 🔹 Формируем сообщение с ключом и значением
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            // 📤 Асинхронно отправляем сообщение и логируем результат
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf("✅ Sent: topic=%s, partition=%d, offset=%d, key=%s, value=%s%n",
                            metadata.topic(), metadata.partition(), metadata.offset(), key, value);
                } else {
                    System.err.println("❌ Error sending message: " + exception.getMessage());
                }
            });
        }

        // 🛑 Завершаем работу продюсера
        producer.flush();  // Ждём, пока все сообщения будут отправлены
        producer.close();  // Закрываем соединение
    }
}
