/*
 * SessionStatus - 会话状态数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 智能体会话状态对象，包含会话ID、状态、当前步骤、进度等信息
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionStatus {
    private String sessionId;     // 会话ID
    private String status;       // 状态（running、completed等）
    private int currentStep;     // 当前步骤
    private int totalSteps;      // 总步骤数
    private String currentAction; // 当前执行的动作
    private double progress;     // 进度百分比
    private long elapsedTimeMs;  // 已用时间（毫秒）
}
