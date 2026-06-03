/*
 * AgentRequest - AI智能体任务请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 智能体任务执行请求对象，包含任务描述、推理策略配置、工具列表等
 */
package com.luoji.ai.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest {
    // 任务描述（必填）
    @NotBlank(message = "Task cannot be blank")
    private String task;
    // 会话ID
    private String sessionId;
    // 指定使用的模型
    private String model;
    // 是否启用任务规划
    private boolean enablePlanning;
    // 是否启用链式思考(Chain of Thought)
    private boolean enableCoT;
    // 是否启用思维树(Tree of Thoughts)
    private boolean enableToT;
    // 最大执行步骤数
    private int maxSteps;
    // 超时时间（秒）
    private long timeoutSeconds;
    // 额外上下文信息
    private Map<String, Object> context;
    // 可用工具列表
    private List<String> availableTools;
}
