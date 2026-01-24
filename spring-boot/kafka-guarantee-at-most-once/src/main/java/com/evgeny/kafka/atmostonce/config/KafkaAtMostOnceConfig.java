package com.evgeny.kafka.atmostonce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaAtMostOnceConfig {

    @Bean
    public NewTopic atMostOnceTopic(@Value("${app.kafka.topic}") String topicName) {
        return new NewTopic(topicName, 3, (short) 1); // 3 партиции, 1 реплика
    }
}