package com.adnanumar.distributed_lovable.intelligence_service.service;

import com.adnanumar.distributed_lovable.intelligence_service.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);

}
