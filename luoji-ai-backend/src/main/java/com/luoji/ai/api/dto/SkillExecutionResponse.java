/*
 * SkillExecutionResponse - 技能执行响应数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 技能执行结果响应对象，包含执行状态、结果和性能信息
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillExecutionResponse {
    private String skillId;         // 技能ID
    private String result;         // 执行结果
    private boolean success;        // 是否成功
    private String errorMessage;    // 错误信息
    private Instant executedAt;     // 执行时间
    private long executionTimeMs;   // 执行耗时（毫秒）
}
