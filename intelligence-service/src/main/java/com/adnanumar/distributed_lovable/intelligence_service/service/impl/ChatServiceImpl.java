package com.adnanumar.distributed_lovable.intelligence_service.service.impl;

import com.adnanumar.distributed_lovable.common_lib.security.AuthUtil;
import com.adnanumar.distributed_lovable.intelligence_service.dto.chat.ChatResponse;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatMessage;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatSession;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatSessionId;
import com.adnanumar.distributed_lovable.intelligence_service.mapper.ChatMapper;
import com.adnanumar.distributed_lovable.intelligence_service.repository.ChatMessageRepository;
import com.adnanumar.distributed_lovable.intelligence_service.repository.ChatSessionRepository;
import com.adnanumar.distributed_lovable.intelligence_service.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatServiceImpl implements ChatService {

    final ChatMessageRepository chatMessageRepository;
    final AuthUtil authUtil;
    final ChatSessionRepository chatSessionRepository;
    final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        log.info("Fetching chat history for project {} by user {}", projectId, userId);

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);

        return chatMapper.fromListOfChatMessage(chatMessageList);
    }

}
