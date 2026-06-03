/*
 * FastQAAgent - 快速问答智能体
 *
 * @author luoji
 * @date 2026-05-28
 * @description 快速问答智能体，处理简单的问答请求，支持同步和流式响应，
 *              自动管理对话历史和上下文
 */
package com.luoji.ai.agent;

import com.luoji.ai.api.dto.ChatRequest;
import com.luoji.ai.api.dto.ChatResponse;
import com.luoji.ai.config.AgentConfig;
import com.luoji.ai.core.llm.LlmClient;
import com.luoji.ai.memory.MemoryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastQAAgent {

    private final LlmClient llmClient;
    private final AgentConfig agentConfig;
    private final MemoryManager memoryManager;

    /**
     * 处理同步问答请求
     * 获取对话历史，构建上下文，调用LLM生成回复
     * @param request 聊天请求
     * @return 聊天响应
     */
    public Mono<ChatResponse> process(ChatRequest request) {
        log.info("Processing fast QA request: {}", request.getMessage());

        return memoryManager.getConversation(request.getSessionId())
                .flatMap(messages -> {
                    List<Message> messageList = new ArrayList<>(messages);
                    messageList.add(new UserMessage(request.getMessage()));

                    // 解析模型配置，使用请求中的模型或默认模型
                    String model = request.getModel() != null
                            ? request.getModel()
                            : agentConfig.getFastQa().getDefaultModel();

                    // 解析温度参数
                    Double temp = request.getTemperature() != null
                            ? request.getTemperature()
                            : 0.7;

                    // 解析最大token数
                    int maxTokens = request.getMaxTokens() != null ? request.getMaxTokens() : 4096;

                    return llmClient.generateWithOptions(
                            request.getMessage(),
                            temp,
                            maxTokens
                    );
                })
                .map(response -> ChatResponse.builder()
                        .message(response)
                        .sessionId(request.getSessionId())
                        .model(request.getModel())
                        .timestamp(Instant.now())
                        .streaming(false)
                        .build())
                // 成功后将消息添加到记忆
                .doOnSuccess(resp -> {
                    if (request.getSessionId() != null) {
                        memoryManager.addMessage(request.getSessionId(), new UserMessage(request.getMessage()));
                        memoryManager.addMessage(request.getSessionId(),
                                new org.springframework.ai.chat.messages.AssistantMessage(resp.getMessage()));
                    }
                });
    }

    /**
     * 处理流式问答请求
     * 使用Server-Sent Events逐块返回响应
     * @param request 聊天请求
     * @return 聊天响应流
     */
    public Flux<ChatResponse> processStream(ChatRequest request) {
        log.info("Processing streaming fast QA request: {}", request.getMessage());

        String model = request.getModel() != null
                ? request.getModel()
                : agentConfig.getFastQa().getDefaultModel();

        Double temp = request.getTemperature() != null ? request.getTemperature() : 0.7;

        return llmClient.generateStream(request.getMessage())
                .map(chunk -> ChatResponse.builder()
                        .message(chunk)
                        .sessionId(request.getSessionId())
                        .model(model)
                        .timestamp(Instant.now())
                        .streaming(true)
                        .build())
                // 流结束后添加用户消息到记忆
                .doOnComplete(() -> {
                    if (request.getSessionId() != null) {
                        memoryManager.addMessage(request.getSessionId(), new UserMessage(request.getMessage()));
                    }
                });
    }
}
