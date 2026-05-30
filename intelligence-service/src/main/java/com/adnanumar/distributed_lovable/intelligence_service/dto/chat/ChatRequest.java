package com.adnanumar.distributed_lovable.intelligence_service.dto.chat;

public record ChatRequest(
        String message,
        Long projectId
) {
}
