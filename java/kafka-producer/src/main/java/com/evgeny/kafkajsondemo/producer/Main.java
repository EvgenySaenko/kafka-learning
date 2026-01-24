package com.evgeny.kafkajsondemo.producer;

public class Main {
    public static void main(String[] args) {
        try (KafkaSimpleProducer producer = new KafkaSimpleProducer("localhost:9092")) {

            // 🔹 Простая отправка (fire-and-forget)
            producer.send("demo-topic", "simple message without key");
            producer.send("demo-topic", "user-123", "message with key");

            // 🔹 Синхронная отправка с ожиданием подтверждения (блокирует поток)
            producer.sendWaitResponse("demo-topic", "sync message without key");

            // 🔹 Асинхронная отправка с callback-ом (не блокирует поток)
            producer.sendAsyncWithCallback("demo-topic", "user-456", "async message with key");

            // 🔸 Подождём чуть-чуть, чтобы callback успел отработать до закрытия продюсера
            Thread.sleep(1000);

        } catch (Exception e) {
            System.err.println("❌ Ошибка при отправке через KafkaSimpleProducer: " + e.getMessage());
        }
    }
}
