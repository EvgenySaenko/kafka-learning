package com.evgeny.kafkajsondemo.producer;

import java.util.Scanner;

public class MainKafkaSimpleProducer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("📥 Enter topic name: ");
        String topic = scanner.nextLine();

        KafkaSimpleProducer producer = new KafkaSimpleProducer("localhost:9092");

        System.out.println("📤 Kafka Producer is ready. Type 'exit' to quit.");
        System.out.println("✍️  Enter messages in format 'key:value' or just 'value':");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();

            if ("exit".equalsIgnoreCase(line)) {
                break;
            }

            String key = null;
            String value = line;

            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                key = parts[0];
                value = parts[1];
            }

            if (key == null) {
                producer.send(topic, value);
            } else {
                producer.send(topic, key, value);
            }

        }

        producer.close();
        scanner.close();
        System.out.println("👋 Kafka Producer stopped.");
    }
}
