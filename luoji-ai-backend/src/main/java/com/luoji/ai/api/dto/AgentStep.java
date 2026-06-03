/*
 * AgentStep - AI智能体执行步骤数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 智能体执行过程中的单步记录，包含思考过程、动作、观察结果和工具调用信息
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentStep {
    private int stepNumber;             // 步骤编号
    private String action;               // 执行的动作
    private String thought;             // 思考过程
    private String observation;         // 观察结果
    private String toolUsed;            // 使用的工具
    private Map<String, Object> toolResult;  // 工具执行结果
    private Instant timestamp;           // 时间戳
}
