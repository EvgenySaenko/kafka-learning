package com.evgeny.kafka.atleastonce.consumer;

import com.evgeny.kafka.atleastonce.dto.MessageDto;
import com.evgeny.kafka.atleastonce.service.MessageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtLeastOnceConsumer {

    private final MessageProcessingService service; // сервис, который сохраняет в БД и может "падать"

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${app.kafka.group-id}")
    public void listen(MessageDto dto, Acknowledgment ack) {

        log.info("📩 RECEIVED key={}, value={}", dto.getKey(), dto.getValue()); // получили сообщение

        service.process(dto); // 1) обработка (сохранение в БД / возможная ошибка)

        ack.acknowledge();    // 2) ACK ТОЛЬКО если process() не упал -> offset будет закоммичен

        log.info("✅ ACKED offset (after DB save)"); // подтверждение, что мы коммитнули offset
    }
}
