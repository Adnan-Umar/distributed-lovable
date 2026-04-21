package com.adnanumar.distributed_lovable.intelligence_service.service.impl;

import com.adnanumar.distributed_lovable.common_lib.enums.ChatEventType;
import com.adnanumar.distributed_lovable.common_lib.enums.MessageRole;
import com.adnanumar.distributed_lovable.common_lib.security.AuthUtil;
import com.adnanumar.distributed_lovable.intelligence_service.client.WorkspaceClient;
import com.adnanumar.distributed_lovable.intelligence_service.dto.chat.StreamResponse;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatEvent;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatMessage;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatSession;
import com.adnanumar.distributed_lovable.intelligence_service.entity.ChatSessionId;
import com.adnanumar.distributed_lovable.intelligence_service.llm.LlmResponseParser;
import com.adnanumar.distributed_lovable.intelligence_service.llm.PromptUtils;
import com.adnanumar.distributed_lovable.intelligence_service.llm.advisors.FileTreeContextAdvisor;
import com.adnanumar.distributed_lovable.intelligence_service.llm.tools.CodeGenerationTools;
import com.adnanumar.distributed_lovable.intelligence_service.repository.ChatEventRepository;
import com.adnanumar.distributed_lovable.intelligence_service.repository.ChatMessageRepository;
import com.adnanumar.distributed_lovable.intelligence_service.repository.ChatSessionRepository;
import com.adnanumar.distributed_lovable.intelligence_service.service.AiGenerationService;
import com.adnanumar.distributed_lovable.intelligence_service.service.UsageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    final ChatClient chatClient;
    final AuthUtil authUtil;
    final FileTreeContextAdvisor fileTreeContextAdvisor;
    final LlmResponseParser llmResponseParser;
    final ChatSessionRepository chatSessionRepository;
    final ChatMessageRepository chatMessageRepository;
    final ChatEventRepository chatEventRepository;
    final UsageService usageService;
    final WorkspaceClient workspaceClient;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {

//        usageService.checkDailyTokensUsage();

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExist(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectId, workspaceClient);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    if (response != null
                            && response.getResult() != null
                            && response.getResult().getOutput() != null
                            && response.getResult().getOutput().getText() != null) {

                        String content = response.getResult().getOutput().getText();

                        if (content != null && !content.isEmpty() && endTime.get() == 0) {
                            endTime.set(System.currentTimeMillis());
                        }

                        usageRef.set(response.getMetadata().getUsage());

                        fullResponseBuffer.append(content);
                    }
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);

                        long duration = (endTime.get() - startTime.get()) / 1000;

                        finalizeChats(userMessage, chatSession, fullResponseBuffer.toString(), duration, usageRef.get());
                    });
                })
                .doOnError(error -> log.error("Error during streaming for projectID: {}", projectId, error))
                .map(response -> {
                    String text = Objects.requireNonNull(response.getResult()).getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                });
    }

    // Utility Methods

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage) {
        Long projectId = chatSession.getId().getProjectId();

        if (usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getId().getUserId(), totalTokens);
        }

        // Save the User message
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokenUsed(usage.getPromptTokens())
                        .build()
        );

        // save automatically because of CascadeType.ALL
        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here...")
                .chatSession(chatSession)
                .tokenUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for " + duration + "s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e -> {
//                    projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()); TODO: kafka
                });

        chatEventRepository.saveAll(chatEventList);
    }

    private ChatSession createChatSessionIfNotExist(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if (chatSession == null) {
            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }

//    private void parseAndSaveFiles(String fullResponse, Long projectId) {
////        String dummy = """
////                <message> I'm going to read the file and generate the code </message>
////                <file path="src/app.jsx">
////                    import app from './App.jsx'
////                    .....
////                </file>
////                <message> I'm going to read the file and generate the code </message>
////                <file path="src/app.jsx">
////                    import app from './App.jsx'
////                    .....
////                </file>
////                """;
//
//        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
//
//        while (matcher.find()) {
//            String filePath = matcher.group(1);
//            String fileContent = matcher.group(2).trim();
//
//            projectFileService.saveFile(projectId, filePath, fileContent);
//        }
//    }

}
