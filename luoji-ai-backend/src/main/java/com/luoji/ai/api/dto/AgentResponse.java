/*
 * AgentResponse - AI智能体响应数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 智能体任务执行响应对象，包含执行结果、状态、时间信息和执行步骤列表
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private String sessionId;             // 会话ID
    private String result;                // 执行结果
    private String status;                // 状态
    private Instant startTime;           // 开始时间
    private Instant endTime;             // 结束时间
    private int stepsTaken;               // 已执行步骤数
    private List<AgentStep> steps;       // 执行步骤列表
    private Map<String, Object> metadata; // 元数据
    private boolean success;              // 是否成功
    private String errorMessage;          // 错误信息
}
