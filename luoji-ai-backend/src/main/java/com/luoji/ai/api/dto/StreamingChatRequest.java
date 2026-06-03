/*
 * StreamingChatRequest - 流式聊天请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 流式聊天请求对象，继承自ChatRequest，用于SSE流式响应场景
 */
package com.luoji.ai.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StreamingChatRequest extends ChatRequest {
    // 用户消息内容（必填）
    @NotBlank(message = "Message cannot be blank")
    private String message;
    private String sessionId;       // 会话ID
    private String model;           // 模型
    private Double temperature;     // 温度
    private Integer maxTokens;      // 最大token
    private Map<String, Object> options;  // 其他选项
    private String userId;          // 用户ID

    /**
     * 创建建造者
     */
    public static StreamingChatRequestBuilder builder() {
        return new StreamingChatRequestBuilder();
    }

    /**
     * 流式请求建造者，继承父类建造者
     */
    public static class StreamingChatRequestBuilder extends ChatRequest.ChatRequestBuilder {
    }
}
