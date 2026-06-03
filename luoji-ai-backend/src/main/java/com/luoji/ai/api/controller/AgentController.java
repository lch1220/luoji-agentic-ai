/*
 * AgentController - AI智能体控制器
 *
 * @author luoji
 * @date 2026-05-28
 * @description AI智能体任务执行控制器，提供复杂任务的规划、执行、状态查询和历史记录功能，
 *              支持链式思考(CoT)和思维树(ToT)等推理策略
 */
package com.luoji.ai.api.controller;

import com.luoji.ai.agent.PlanningAgent;
import com.luoji.ai.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/agent")
@RequiredArgsConstructor
@Tag(name = "Agent", description = "AI Agent task execution API")
public class AgentController {

    private final PlanningAgent planningAgent;

    /**
     * 执行AI智能体任务
     * 使用规划智能体分解并执行复杂任务
     * @param request 任务请求
     * @return 任务执行响应
     */
    @PostMapping("/execute")
    @Operation(summary = "Execute a task using the planning agent")
    public Mono<AgentResponse> execute(@RequestBody AgentRequest request) {
        log.info("Executing agent task: {}", request.getTask());
        return planningAgent.execute(request);
    }

    /**
     * 流式执行AI智能体任务
     * 通过SSE逐个返回执行步骤
     * @param request 任务请求
     * @return 执行步骤SSE流
     */
    @PostMapping(value = "/execute/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Execute a task with streaming response")
    public Flux<ServerSentEvent<AgentStep>> executeStream(@RequestBody AgentRequest request) {
        log.info("Streaming agent task: {}", request.getTask());
        return planningAgent.executeWithSteps(request)
                .map(step -> ServerSentEvent.<AgentStep>builder()
                        .data(step)
                        .build());
    }

    /**
     * 获取智能体会话状态
     * @param sessionId 会话ID
     * @return 会话状态信息
     */
    @GetMapping("/status/{sessionId}")
    @Operation(summary = "Get agent session status")
    public Mono<SessionStatus> getStatus(@PathVariable String sessionId) {
        return planningAgent.getSessionStatus(sessionId);
    }

    /**
     * 停止智能体会话
     * @param sessionId 会话ID
     * @return 是否成功停止
     */
    @PostMapping("/stop/{sessionId}")
    @Operation(summary = "Stop an agent session")
    public Mono<Boolean> stopSession(@PathVariable String sessionId) {
        log.info("Stopping agent session: {}", sessionId);
        return planningAgent.stopSession(sessionId);
    }

    /**
     * 获取智能体执行历史
     * @param sessionId 会话ID
     * @return 执行步骤历史列表
     */
    @GetMapping("/history/{sessionId}")
    @Operation(summary = "Get agent execution history")
    public Mono<List<AgentStep>> getHistory(@PathVariable String sessionId) {
        return planningAgent.getExecutionHistory(sessionId);
    }
}
