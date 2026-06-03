/*
 * SkillExecutionRequest - 技能执行请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 技能执行请求对象，包含执行所需的参数
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillExecutionRequest {
    // 技能执行参数映射
    private Map<String, Object> parameters;
}
