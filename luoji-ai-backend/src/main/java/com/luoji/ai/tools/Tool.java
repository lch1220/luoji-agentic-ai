/*
 * Tool - AI工具定义
 *
 * @author luoji
 * @date 2026-05-28
 * @description 工具定义类，描述AI智能体可调用的外部工具，
 *              包含工具ID、名称、参数定义和处理函数
 */
package com.luoji.ai.tools;

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
public class Tool {
    private String id;                 // 工具唯一标识
    private String name;               // 工具名称
    private String description;        // 工具描述
    private String category;          // 工具分类
    private Map<String, ToolParameter> parameters;  // 参数定义
    private Function<Map<String, Object>, Object> handler;  // 处理函数
    private boolean isAsync;           // 是否异步执行

    /**
     * 工具参数定义内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolParameter {
        private String name;          // 参数名
        private String type;         // 参数类型
        private String description;   // 参数描述
        private boolean required;     // 是否必填
        private Object defaultValue;  // 默认值
    }
}
