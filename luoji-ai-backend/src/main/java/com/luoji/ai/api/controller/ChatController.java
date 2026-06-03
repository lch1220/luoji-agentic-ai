/*
 * ChatController - 聊天控制器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 聊天API控制器，提供快速问答的同步和流式接口，
 *              处理用户消息并返回AI生成的回复
 */
package com.luoji.ai.api.controller;

import com.luoji.ai.agent.FastQAAgent;
import com.luoji.ai.api.dto.ChatRequest;
import com.luoji.ai.api.dto.ChatResponse;
import com.luoji.ai.api.dto.StreamingChatRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat API for fast Q&A")
public class ChatController {

    private final FastQAAgent fastQAAgent;

    /**
     * 处理同步聊天消息
     * 接收用户消息，通过FastQAAgent处理并返回完整响应
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping
    @Operation(summary = "Send a chat message", description = "Send a message and get a response")
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: {}", request.getMessage());
        return fastQAAgent.process(request);
    }

    /**
     * 处理流式聊天消息
     * 使用Server-Sent Events（SSE）协议返回流式响应
     * @param request 流式聊天请求
     * @return SSE事件流
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Send a streaming chat message", description = "Send a message and get a streaming response")
    public Flux<ServerSentEvent<ChatResponse>> streamChat(@RequestBody StreamingChatRequest request) {
        log.info("Received streaming chat request: {}", request.getMessage());
        return fastQAAgent.processStream(request)
                .map(response -> ServerSentEvent.<ChatResponse>builder()
                        .data(response)
                        .build());
    }

    /**
     * 健康检查接口
     * @return 服务健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "Check chat service health")
    public Mono<String> health() {
        return Mono.just("OK");
    }
}
