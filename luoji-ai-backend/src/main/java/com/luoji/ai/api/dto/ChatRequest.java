/*
 * ChatRequest - 聊天请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 聊天API请求对象，包含消息内容、会话ID、模型参数等配置选项
 */
package com.luoji.ai.api.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ChatRequest {
    // 用户消息内容（必填）
    @NotBlank(message = "Message cannot be blank")
    private String message;
    // 会话ID，用于关联对话历史
    private String sessionId;
    // 指定使用的模型（可选）
    private String model;
    // 生成温度参数（可选）
    private Double temperature;
    // 最大生成的token数（可选）
    private Integer maxTokens;
    // 其他可选参数
    private Map<String, Object> options;
    // 用户ID
    private String userId;
}
