package com.adnanumar.distributed_lovable.intelligence_service.consumer;

import com.adnanumar.distributed_lovable.common_lib.enums.ChatEventStatus;
import com.adnanumar.distributed_lovable.common_lib.event.FileStoreResponseEvent;
import com.adnanumar.distributed_lovable.intelligence_service.repository.ChatEventRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntelligenceSagaResponseHandler {

    final ChatEventRepository chatEventRepository;

    @Transactional
    @KafkaListener(topics = "file-store-responses-event-topic", groupId = "intelligence-group")
    public void handleSagaResponse(FileStoreResponseEvent response) {

        chatEventRepository.findBySagaId(response.sagaId()).ifPresent(event -> {

            if (!ChatEventStatus.PENDING.equals(event.getStatus())) {   // Idempotency
                log.info("Response for saga {} already handled. Skipped.", response.sagaId());
                return;
            }

            if (response.success()) {
                event.setStatus(ChatEventStatus.CONFIRMED);
                log.info("Saga {} CONFIRMED.", response.sagaId());
            } else {
                log.warn("Saga {} FAILED. Deleting event.", response.sagaId());
                event.setStatus(ChatEventStatus.FAILED);
            }
        });
    }

}
