package com.evgeny.kafka.atleastonceidempotent.service;

import com.evgeny.kafka.atleastonceidempotent.dto.MessageDto;
import com.evgeny.kafka.atleastonceidempotent.entity.ReceivedMessage;
import com.evgeny.kafka.atleastonceidempotent.repository.ReceivedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessingService {

    private final ReceivedMessageRepository repository;

    public void process(MessageDto dto) {

        // ✅ 0) базовая валидация (для демо)
        if (dto.getMessageId() == null || dto.getMessageId().isBlank()) {
            throw new IllegalArgumentException("messageId is required for idempotency");
        }

        // ✅ 1) Падаем ДО сохранения -> будет retry -> потом DLT (и в БД не появится)
        if ("FAIL_BEFORE_SAVE".equalsIgnoreCase(dto.getValue())) {
            log.error("💥 FAIL_BEFORE_SAVE: падаем ДО сохранения");
            throw new RuntimeException("Simulated crash BEFORE save");
        }

        // ✅ 2) Быстрый путь: если уже было — ничего не делаем
        if (repository.existsByMessageId(dto.getMessageId())) {
            log.warn("🔁 DUPLICATE detected by exists-check, messageId={} -> skip", dto.getMessageId());
            return;
        }

        // ✅ 3) Пытаемся сохранить (БД — главный “guard” от гонок/дубликатов)
        try {
            ReceivedMessage entity = ReceivedMessage.builder()
                    .messageId(dto.getMessageId())
                    .messageKey(dto.getKey())
                    .messageValue(dto.getValue())
                    .receivedAt(LocalDateTime.now())
                    .build();

            ReceivedMessage saved = repository.save(entity);
            log.info("💾 SAVED to DB id={}, messageId={}, key={}, value={}",
                    saved.getId(), dto.getMessageId(), dto.getKey(), dto.getValue());
        } catch (DataIntegrityViolationException e) {
            // ✅ если два потока/рестарт/повтор -> БД скажет "уже есть"
            log.warn("🔁 DUPLICATE detected by UNIQUE(message_id), messageId={} -> treat as success",
                    dto.getMessageId());
            return;
        }

        // ✅ 4) Падаем ПОСЛЕ сохранения:
        // offset не ack-нут -> retry будет, но запись в БД второй раз НЕ появится (idempotent)
        if ("FAIL_AFTER_SAVE".equalsIgnoreCase(dto.getValue())) {
            log.error("💥 FAIL_AFTER_SAVE: падаем ПОСЛЕ сохранения (но дубля в БД не будет)");
            throw new RuntimeException("Simulated crash AFTER save");
        }
    }
}
