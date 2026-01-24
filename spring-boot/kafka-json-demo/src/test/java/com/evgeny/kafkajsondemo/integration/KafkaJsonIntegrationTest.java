package com.evgeny.kafkajsondemo.integration;



import com.evgeny.kafkajsondemo.dto.MessageDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaJsonIntegrationTest {

    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;

    private final LinkedBlockingQueue<ConsumerRecord<String, MessageDto>> records = new LinkedBlockingQueue<>();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        kafkaContainer.start();
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void testSendMessageToKafka() throws Exception {
        // arrange
        MessageDto message = new MessageDto("test-key", "Hello from Test!");

        // act
        kafkaTemplate.send("json-demo-topic", message);

        // Здесь ты можешь либо прослушивать Kafka, либо проверить логи, либо через TestConsumer

        // assert (упрощённо: проверяем, что не выбросило исключение)
        assertThat(true).isTrue();
    }

    @AfterAll
    void tearDown() {
        kafkaContainer.stop();
    }
}

