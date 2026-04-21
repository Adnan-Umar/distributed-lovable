package com.adnanumar.distributed_lovable.intelligence_service.controller;

import com.adnanumar.distributed_lovable.intelligence_service.dto.chat.ChatRequest;
import com.adnanumar.distributed_lovable.intelligence_service.dto.chat.ChatResponse;
import com.adnanumar.distributed_lovable.intelligence_service.dto.chat.StreamResponse;
import com.adnanumar.distributed_lovable.intelligence_service.service.AiGenerationService;
import com.adnanumar.distributed_lovable.intelligence_service.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    final AiGenerationService aiGenerationService;
    final ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamResponse>> streamChat(@RequestBody ChatRequest request) {
        return aiGenerationService.streamResponse(request.message(), request.projectId())
                .map(data -> ServerSentEvent.<StreamResponse>builder()
                        .data(data)
                        .build());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId) {
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }

}
