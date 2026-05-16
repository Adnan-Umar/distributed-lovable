package com.adnanumar.distributed_lovable.workspace_service.consumer;

import com.adnanumar.distributed_lovable.common_lib.event.FileStoreRequestEvent;
import com.adnanumar.distributed_lovable.workspace_service.service.ProjectFileService;
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
public class FileStorageConsumer {

    final ProjectFileService projectFileService;

    @KafkaListener(topics = "file-storage-request-event-topic", groupId = "workspace-group")
    public void consumeFileEvent(FileStoreRequestEvent requestEvent) {
        log.info("Saving file : {}", requestEvent.filePath());
        projectFileService.saveFile(requestEvent.projectId(), requestEvent.filePath(), requestEvent.content());
    }

}
