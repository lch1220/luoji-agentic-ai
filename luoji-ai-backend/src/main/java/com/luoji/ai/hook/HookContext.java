/*
 * HookContext - 钩子上下文
 *
 * @author luoji
 * @date 2026-05-28
 * @description 钩子上下文对象，在钩子触发时传递给钩子处理函数，
 *              包含会话信息、消息内容和元数据
 */
package com.luoji.ai.hook;

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
public class HookContext {
    private String sessionId;        // 会话ID
    private String userId;          // 用户ID
    private String message;          // 消息内容
    private Map<String, Object> metadata;  // 元数据
    private Instant timestamp;       // 时间戳
    private Hook.HookType hookType;  // 钩子类型
}
