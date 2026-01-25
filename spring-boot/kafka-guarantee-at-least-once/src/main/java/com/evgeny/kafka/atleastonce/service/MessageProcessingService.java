package com.evgeny.kafka.atleastonce.service;

import com.evgeny.kafka.atleastonce.dto.MessageDto;
import com.evgeny.kafka.atleastonce.entity.ReceivedMessage;
import com.evgeny.kafka.atleastonce.repository.ReceivedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessingService {

    private final ReceivedMessageRepository repository; // JPA-репозиторий

    public void process(MessageDto dto) {

        // 1) Падаем ДО сохранения => в БД НЕ попадёт, в Kafka offset НЕ ack-нут => будет retry => потом DLT
        if ("FAIL_BEFORE_SAVE".equalsIgnoreCase(dto.getValue())) {
            log.error("💥 FAIL_BEFORE_SAVE: падаем ДО сохранения");
            throw new RuntimeException("Simulated crash BEFORE save");
        }

        // 2) Сохраняем в БД (как будто бизнес-обработка)
        ReceivedMessage entity = ReceivedMessage.builder()
                .messageKey(dto.getKey())        // поле messageKey, чтобы не конфликтовать с SQL keyword
                .messageValue(dto.getValue())    // НЕ называй колонку `value`, это keyword в H2/SQL
                .receivedAt(LocalDateTime.now())
                .build();

        ReceivedMessage saved = repository.save(entity); // сохранение в БД
        log.info("💾 SAVED to DB id={}, key={}, value={}", saved.getId(), dto.getKey(), dto.getValue());

        // 3) Падаем ПОСЛЕ сохранения => запись в БД уже есть, но ACK не случился => retry => дубликаты в БД (at-least-once)
        if ("FAIL_AFTER_SAVE".equalsIgnoreCase(dto.getValue())) {
            log.error("💥 FAIL_AFTER_SAVE: падаем ПОСЛЕ сохранения (дубликаты неизбежны)");
            throw new RuntimeException("Simulated crash AFTER save");
        }
    }
}
