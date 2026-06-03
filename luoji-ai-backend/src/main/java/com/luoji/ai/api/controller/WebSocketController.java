/*
 * WebSocketController - WebSocket控制器
 *
 * @author luoji
 * @date 2026-05-28
 * @description WebSocket实时聊天控制器，处理WebSocket连接上的聊天消息，
 *              支持同步和流式消息处理，实现实时双向通信
 */
package com.luoji.ai.api.controller;

import com.luoji.ai.api.dto.ChatRequest;
import com.luoji.ai.api.dto.ChatResponse;
import com.luoji.ai.session.SessionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket", description = "WebSocket real-time chat API")
public class WebSocketController {

    private final SessionManager sessionManager;

    /**
     * 处理WebSocket聊天消息（同步）
     * 消息发送到/topic/chat主题
     * @param request 聊天请求
     * @return 聊天响应
     */
    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    @Operation(summary = "Send chat message via WebSocket")
    public Mono<ChatResponse> handleChatMessage(@Payload ChatRequest request) {
        log.info("Received WebSocket message: {}", request.getMessage());
        // 如果没有sessionId则生成一个新的
        String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();
        request.setSessionId(sessionId);

        return sessionManager.getSession(sessionId)
                .flatMap(s -> sessionManager.processMessage(sessionId, request.getMessage()))
                .map(response -> ChatResponse.builder()
                        .message(response)
                        .sessionId(sessionId)
                        .timestamp(Instant.now())
                        .build());
    }

    /**
     * 处理WebSocket流式聊天消息
     * @param request 聊天请求
     * @return 聊天响应流
     */
    @MessageMapping("/chat/stream")
    @Operation(summary = "Send streaming chat message via WebSocket")
    public Flux<ChatResponse> handleStreamingChat(@Payload ChatRequest request) {
        log.info("Received WebSocket streaming message: {}", request.getMessage());
        String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();
        request.setSessionId(sessionId);

        return sessionManager.getSession(sessionId)
                .flatMapMany(s -> sessionManager.processMessageStream(sessionId, request.getMessage()))
                .map(response -> ChatResponse.builder()
                        .message(response)
                        .sessionId(sessionId)
                        .timestamp(Instant.now())
                        .build());
    }
}
