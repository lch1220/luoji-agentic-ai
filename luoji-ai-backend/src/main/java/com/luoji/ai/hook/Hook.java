/*
 * Hook - 钩子定义
 *
 * @author luoji
 * @date 2026-05-28
 * @description 钩子定义类，描述AI系统生命周期中的回调点，
 *              支持预处理、后处理、错误处理等多种钩子类型
 */
package com.luoji.ai.hook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hook {
    private String id;                             // 钩子唯一标识
    private String name;                           // 钩子名称
    private HookType type;                         // 钩子类型
    private int priority;                          // 优先级（数值越大优先级越高）
    private Function<HookContext, HookResult> handler;  // 处理函数
    private boolean enabled;                       // 是否启用

    /**
     * 钩子类型枚举
     * 定义钩子在整个AI系统生命周期中的触发点
     */
    public enum HookType {
        PRE_PROCESS,    // 预处理，在主要逻辑执行前调用
        POST_PROCESS,   // 后处理，在主要逻辑执行后调用
        ON_ERROR,       // 错误处理，发生错误时调用
        ON_TOOL_CALL,   // 工具调用，执行工具时调用
        ON_SESSION_START, // 会话开始时调用
        ON_SESSION_END  // 会话结束时调用
    }
}
