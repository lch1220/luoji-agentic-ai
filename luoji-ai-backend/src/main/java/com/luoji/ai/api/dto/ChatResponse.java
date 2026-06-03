/*
 * ChatResponse - 聊天响应数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 聊天API响应对象，包含AI生成的回复内容、模型信息、时间戳、
 *              流式标记、结束原因以及token使用统计
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
public class ChatResponse {
    // AI生成的回复内容
    private String message;
    // 关联的会话ID
    private String sessionId;
    // 使用的模型名称
    private String model;
    // 响应时间戳
    private Instant timestamp;
    // 是否为流式响应
    private boolean streaming;
    // 结束原因（如stop、length等）
    private String finishReason;
    // Token使用统计信息
    private UsageInfo usage;

    /**
     * Token使用统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageInfo {
        private int promptTokens;      // 提示词token数
        private int completionTokens;  // 生成token数
        private int totalTokens;      // 总token数
    }
}
