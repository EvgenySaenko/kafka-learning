package com.evgeny.kafkajsondemo.service;

import com.evgeny.kafkajsondemo.dto.MessageDto;
import com.evgeny.kafkajsondemo.entity.ReceivedMessage;
import com.evgeny.kafkajsondemo.repository.ReceivedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessingService {
    private final ReceivedMessageRepository repository;


//    // эмулируем ошибку
//    public void processMessage(String key, MessageDto message) {
//        if ("fail".equalsIgnoreCase(key)) {
//            throw new RuntimeException("💥 Искусственная ошибка обработки!");
//        }
//
//        log.info("✅ Сообщение обработано: key={}, value={}", key, message.getValue());
//    }

    public void processMessage(MessageDto message) {
        log.info("✅ Сохраняем сообщение в БД: key={}, value={}", message.getKey(), message.getValue());

        var entity = ReceivedMessage.builder()
                .messageKey(message.getKey())
                .payload(message.getValue())
                .receivedAt(LocalDateTime.now())
                .build();

        repository.save(entity);

        // 🎯 Дополнительно: можно добавить валидацию или искусственную ошибку
    }

}