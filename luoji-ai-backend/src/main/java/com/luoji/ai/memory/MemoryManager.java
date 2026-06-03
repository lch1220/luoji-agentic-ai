/*
 * MemoryManager - 记忆管理器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 记忆管理器，管理对话历史的存储和检索，
 *              支持对话历史获取、添加、清除和语义搜索
 */
package com.luoji.ai.memory;

import com.luoji.ai.config.MemoryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemoryManager {

    private final MemoryConfig memoryConfig;

    // In-memory storage for development - use Redis/MongoDB in production
    // 内存存储用于开发，生产环境应使用Redis/MongoDB
    private final Map<String, List<Message>> conversationMemory = new ConcurrentHashMap<>();

    /**
     * 获取对话历史
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public Mono<List<Message>> getConversation(String sessionId) {
        List<Message> messages = conversationMemory.getOrDefault(sessionId, new ArrayList<>());
        return Mono.just(messages);
    }

    /**
     * 添加消息到对话历史
     * 自动维护最大对话长度限制
     * @param sessionId 会话ID
     * @param message 消息
     * @return 完成信号
     */
    public Mono<Void> addMessage(String sessionId, Message message) {
        conversationMemory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);

        // Enforce max conversation length
        // 强制执行最大对话长度限制
        List<Message> messages = conversationMemory.get(sessionId);
        if (messages.size() > memoryConfig.getMaxConversationLength()) {
            List<Message> trimmed = messages.subList(
                    messages.size() - memoryConfig.getMaxConversationLength(),
                    messages.size()
            );
            conversationMemory.put(sessionId, new ArrayList<>(trimmed));
        }

        return Mono.empty();
    }

    /**
     * 清除对话历史
     * @param sessionId 会话ID
     * @return 完成信号
     */
    public Mono<Void> clearConversation(String sessionId) {
        conversationMemory.remove(sessionId);
        return Mono.empty();
    }

    /**
     * 语义搜索对话历史
     * @param sessionId 会话ID
     * @param query 查询内容
     * @return 相关消息列表
     */
    public Mono<List<Message>> searchSemanticMemory(String sessionId, String query) {
        // Mock semantic search - use vector similarity in production
        // 模拟语义搜索，生产环境应使用向量相似性搜索
        return getConversation(sessionId);
    }

    /**
     * 生成对话摘要
     * @param sessionId 会话ID
     * @return 摘要文本
     */
    public Mono<String> summarizeConversation(String sessionId) {
        return getConversation(sessionId)
                .map(messages -> {
                    if (messages.isEmpty()) {
                        return "Empty conversation";
                    }
                    return "Conversation summary: " + messages.size() + " messages";
                });
    }

    /**
     * 获取对话流
     * @param sessionId 会话ID
     * @return 消息流
     */
    public Flux<Message> getConversationStream(String sessionId) {
        return Flux.fromIterable(conversationMemory.getOrDefault(sessionId, new ArrayList<>()));
    }
}
