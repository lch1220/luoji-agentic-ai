/*
 * SessionManager - 会话管理器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 会话管理器，管理用户会话的创建、状态维护和消息处理，
 *              支持同步和流式消息处理
 */
package com.luoji.ai.session;

import com.luoji.ai.agent.FastQAAgent;
import com.luoji.ai.api.dto.ChatRequest;
import com.luoji.ai.api.dto.ChatResponse;
import com.luoji.ai.config.SessionConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionConfig sessionConfig;
    private final FastQAAgent fastQAAgent;

    // 会话存储映射
    private final Map<String, SessionState> sessions = new ConcurrentHashMap<>();

    /**
     * 会话状态内部类
     * 跟踪会话的生命周期信息
     */
    @Data
    public static class SessionState {
        private String sessionId;
        private SessionConfig.SessionState state;
        private Instant createdAt;
        private Instant lastActiveAt;
        private int messageCount;
        private Object data;
    }

    /**
     * 创建新会话
     * @param sessionId 会话ID
     * @return 会话状态
     */
    public Mono<SessionState> createSession(String sessionId) {
        SessionState state = new SessionState();
        state.setSessionId(sessionId);
        state.setState(SessionConfig.SessionState.INIT);
        state.setCreatedAt(Instant.now());
        state.setLastActiveAt(Instant.now());
        state.setMessageCount(0);

        sessions.put(sessionId, state);
        log.info("Created session: {}", sessionId);

        return Mono.just(state);
    }

    /**
     * 获取会话
     * 如果不存在则创建新会话
     * @param sessionId 会话ID
     * @return 会话状态
     */
    public Mono<SessionState> getSession(String sessionId) {
        SessionState state = sessions.get(sessionId);
        if (state == null) {
            return createSession(sessionId);
        }
        state.setLastActiveAt(Instant.now());
        return Mono.just(state);
    }

    /**
     * 更新会话状态
     * @param sessionId 会话ID
     * @param newState 新状态
     * @return 完成信号
     */
    public Mono<Void> updateSessionState(String sessionId, SessionConfig.SessionState newState) {
        SessionState state = sessions.get(sessionId);
        if (state != null) {
            state.setState(newState);
            state.setLastActiveAt(Instant.now());
        }
        return Mono.empty();
    }

    /**
     * 处理同步消息
     * @param sessionId 会话ID
     * @param message 消息内容
     * @return AI响应
     */
    public Mono<String> processMessage(String sessionId, String message) {
        return getSession(sessionId)
                .flatMap(state -> {
                    state.setMessageCount(state.getMessageCount() + 1);
                    state.setState(SessionConfig.SessionState.ACTIVE);
                    state.setLastActiveAt(Instant.now());

                    ChatRequest request = ChatRequest.builder()
                            .message(message)
                            .sessionId(sessionId)
                            .build();

                    return fastQAAgent.process(request);
                })
                .map(response -> {
                    updateSessionState(sessionId, SessionConfig.SessionState.WAITING_FOR_INPUT);
                    return response.getMessage();
                });
    }

    /**
     * 处理流式消息
     * @param sessionId 会话ID
     * @param message 消息内容
     * @return AI响应流
     */
    public Flux<String> processMessageStream(String sessionId, String message) {
        return getSession(sessionId)
                .flatMapMany(state -> {
                    state.setMessageCount(state.getMessageCount() + 1);
                    state.setState(SessionConfig.SessionState.ACTIVE);
                    state.setLastActiveAt(Instant.now());

                    ChatRequest request = ChatRequest.builder()
                            .message(message)
                            .sessionId(sessionId)
                            .build();

                    return fastQAAgent.processStream(request)
                            .map(ChatResponse::getMessage);
                });
    }

    /**
     * 关闭会话
     * @param sessionId 会话ID
     * @return 是否成功关闭
     */
    public Mono<Boolean> closeSession(String sessionId) {
        SessionState state = sessions.remove(sessionId);
        if (state != null) {
            log.info("Closed session: {} with {} messages", sessionId, state.getMessageCount());
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    /**
     * 获取活跃会话数
     * @return 会话数量
     */
    public Mono<Integer> getActiveSessionCount() {
        return Mono.just(sessions.size());
    }

    /**
     * 获取所有会话
     * @return 会话流
     */
    public Flux<SessionState> getAllSessions() {
        return Flux.fromIterable(sessions.values());
    }
}
