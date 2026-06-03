/*
 * PlanningAgent - 规划智能体
 *
 * @author luoji
 * @date 2026-05-28
 * @description 规划智能体，处理复杂任务的分解和执行，支持链式思考(CoT)、
 *              思维树(ToT)等推理策略，返回分步执行结果
 */
package com.luoji.ai.agent;

import com.luoji.ai.api.dto.AgentRequest;
import com.luoji.ai.api.dto.AgentResponse;
import com.luoji.ai.api.dto.AgentStep;
import com.luoji.ai.config.AgentConfig;
import com.luoji.ai.core.llm.LlmClient;
import com.luoji.ai.core.llm.ReasoningEngine;
import com.luoji.ai.tools.ToolRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanningAgent {

    private final LlmClient llmClient;
    private final ReasoningEngine reasoningEngine;
    private final AgentConfig agentConfig;
    private final ToolRegistry toolRegistry;

    // 活跃会话映射，用于跟踪正在执行的任务
    private final Map<String, SessionState> activeSessions = new ConcurrentHashMap<>();

    /**
     * 内部会话状态类
     * 跟踪智能体执行过程中的状态信息
     */
    @lombok.Data
    private static class SessionState {
        String sessionId;
        String task;
        List<AgentStep> steps;
        Instant startTime;
        boolean completed;
    }

    /**
     * 执行规划任务（同步）
     * 创建会话状态，根据配置启用CoT等推理策略
     * @param request 任务请求
     * @return 任务执行响应
     */
    public Mono<AgentResponse> execute(AgentRequest request) {
        log.info("Executing planning agent task: {}", request.getTask());

        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        SessionState state = new SessionState();
        state.setSessionId(sessionId);
        state.setTask(request.getTask());
        state.setSteps(new ArrayList<>());
        state.setStartTime(Instant.now());

        activeSessions.put(sessionId, state);

        int maxSteps = request.getMaxSteps() > 0
                ? request.getMaxSteps()
                : agentConfig.getPlanning().getMaxSteps();

        return executeSteps(request, state, maxSteps)
                .map(steps -> AgentResponse.builder()
                        .sessionId(sessionId)
                        .result(generateFinalResult(steps))
                        .status("completed")
                        .startTime(state.getStartTime())
                        .endTime(Instant.now())
                        .stepsTaken(steps.size())
                        .steps(steps)
                        .success(true)
                        .build());
    }

    /**
     * 执行规划任务（流式）
     * 逐个返回执行步骤
     * @param request 任务请求
     * @return 执行步骤流
     */
    public Flux<AgentStep> executeWithSteps(AgentRequest request) {
        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        SessionState state = new SessionState();
        state.setSessionId(sessionId);
        state.setTask(request.getTask());
        state.setSteps(new ArrayList<>());
        state.setStartTime(Instant.now());

        activeSessions.put(sessionId, state);

        return executeStepsStream(request, state);
    }

    /**
     * 执行任务步骤（同步）
     * 根据配置使用链式思考进行推理
     */
    private Mono<List<AgentStep>> executeSteps(AgentRequest request, SessionState state, int maxSteps) {
        if (request.isEnableCoT() && agentConfig.getPlanning().isEnableCoT()) {
            return reasoningEngine.reasonWithCoT(request.getTask())
                    .map(reasoning -> {
                        AgentStep step = AgentStep.builder()
                                .stepNumber(1)
                                .thought(reasoning)
                                .action("reasoning")
                                .timestamp(Instant.now())
                                .build();
                        state.getSteps().add(step);
                        return new ArrayList<>(state.getSteps());
                    });
        }

        return Mono.just(new ArrayList<>(state.getSteps()));
    }

    /**
     * 执行任务步骤（流式）
     */
    private Flux<AgentStep> executeStepsStream(AgentRequest request, SessionState state) {
        if (request.isEnablePlanning() || request.isEnableCoT()) {
            return reasoningEngine.reasonWithCoT(request.getTask())
                    .flux()
                    .map(reasoning -> AgentStep.builder()
                            .stepNumber(1)
                            .thought(reasoning)
                            .action("reasoning")
                            .timestamp(Instant.now())
                            .build())
                    .doOnNext(state.getSteps()::add);
        }

        return Flux.empty();
    }

    /**
     * 获取会话状态
     * @param sessionId 会话ID
     * @return 会话状态信息
     */
    public Mono<com.luoji.ai.api.dto.SessionStatus> getSessionStatus(String sessionId) {
        SessionState state = activeSessions.get(sessionId);
        if (state == null) {
            return Mono.empty();
        }

        double progress = state.isCompleted() ? 100.0 : (state.getSteps().size() * 100.0 / agentConfig.getPlanning().getMaxSteps());
        long elapsed = Duration.between(state.getStartTime(), Instant.now()).toMillis();

        return Mono.just(com.luoji.ai.api.dto.SessionStatus.builder()
                .sessionId(sessionId)
                .status(state.isCompleted() ? "completed" : "running")
                .currentStep(state.getSteps().size())
                .totalSteps(agentConfig.getPlanning().getMaxSteps())
                .currentAction(state.getSteps().isEmpty() ? "init" : state.getSteps().get(state.getSteps().size() - 1).getAction())
                .progress(progress)
                .elapsedTimeMs(elapsed)
                .build());
    }

    /**
     * 停止会话
     * @param sessionId 会话ID
     * @return 是否成功停止
     */
    public Mono<Boolean> stopSession(String sessionId) {
        SessionState state = activeSessions.get(sessionId);
        if (state != null) {
            state.setCompleted(true);
            log.info("Stopped agent session: {}", sessionId);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    /**
     * 获取执行历史
     * @param sessionId 会话ID
     * @return 执行步骤列表
     */
    public Mono<List<AgentStep>> getExecutionHistory(String sessionId) {
        SessionState state = activeSessions.get(sessionId);
        if (state == null) {
            return Mono.just(List.of());
        }
        return Mono.just(new ArrayList<>(state.getSteps()));
    }

    /**
     * 生成最终结果文本
     * 将执行步骤格式化为可读字符串
     */
    private String generateFinalResult(List<AgentStep> steps) {
        if (steps.isEmpty()) {
            return "No steps were executed.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Task completed with ").append(steps.size()).append(" steps:\n\n");

        for (AgentStep step : steps) {
            sb.append("Step ").append(step.getStepNumber()).append(": ").append(step.getAction()).append("\n");
            if (step.getThought() != null) {
                sb.append("  Thought: ").append(step.getThought()).append("\n");
            }
            if (step.getObservation() != null) {
                sb.append("  Observation: ").append(step.getObservation()).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
