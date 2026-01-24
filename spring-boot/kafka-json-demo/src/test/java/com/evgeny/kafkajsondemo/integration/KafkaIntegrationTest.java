package com.evgeny.kafkajsondemo.integration;

import com.evgeny.kafkajsondemo.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
public class KafkaIntegrationTest {

    // 🧪 Поднимаем Kafka-контейнер через Testcontainers
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));

    static {
        kafkaContainer.start(); // ⚡ Старт контейнера Kafka перед запуском тестов
    }

    // ⚙️ Прокидываем адрес Kafka в Spring Boot контекст через dynamic properties
    @DynamicPropertySource
    static void configureKafka(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;

    // ✅ Тестируем отправку и получение JSON-сообщения
    @Test
    void testKafkaJsonMessaging() throws Exception {
        // 📨 Создаём сообщение
        MessageDto message = new MessageDto("test-key", "Hello from Test");

        // 📤 Отправляем сообщение в Kafka
        kafkaTemplate.send("json-demo-topic", message.getKey(), message);

        // 💤 Даём Kafka чуть времени на доставку (лучше использовать Awaitility, но можно и sleep)
        TimeUnit.SECONDS.sleep(2);

        // 📝 Здесь мы могли бы протестировать что Consumer вызвал метод или сохранил в БД
        // Но в этом простом примере проверим, что ошибок не возникло

        // ℹ️ Альтернатива: Можно поднять test consumer и прочитать сообщение вручную

        log.info("✅ Тест Kafka JSON отправки завершён успешно.");
    }
}
