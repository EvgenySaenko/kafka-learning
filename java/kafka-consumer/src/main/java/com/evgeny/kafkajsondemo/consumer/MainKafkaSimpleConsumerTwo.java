package com.evgeny.kafkajsondemo.consumer;

public class MainKafkaSimpleConsumerTwo {
    public static void main(String[] args) {
        KafkaSimpleConsumer consumer = new KafkaSimpleConsumer(
                "localhost:9092",
                "demo-topic-v2",
                "java-consumer-group",
                "latest", // latest / earliest
                "false"   // ручной коммит
        );
        System.out.println("🔥 Second Kafka consumer is starting...");
        consumer.pollMessages(); // старт прослушивания
    }
}
