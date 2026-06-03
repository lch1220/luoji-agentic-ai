/*
 * ToolRegistry - 工具注册表
 *
 * @author luoji
 * @date 2026-05-28
 * @description 工具注册表，管理AI智能体可使用的所有工具，
 *              提供工具的注册、注销、查询和执行功能
 */
package com.luoji.ai.tools;

import com.luoji.ai.api.dto.SkillInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistry {

    // 工具存储映射
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();

    /**
     * 初始化注册表
     * 注册内置工具
     */
    @PostConstruct
    public void init() {
        registerBuiltInTools();
    }

    /**
     * 注册内置工具
     * 包括Web搜索、计算器、获取当前时间等工具
     */
    private void registerBuiltInTools() {
        register(Tool.builder()
                .id("web_search")
                .name("Web Search")
                .description("Search the web for information")
                .category("search")
                .parameters(Map.of(
                        "query", Tool.ToolParameter.builder()
                                .name("query")
                                .type("string")
                                .description("Search query")
                                .required(true)
                                .build()
                ))
                .handler(params -> "Mock web search results for: " + params.get("query"))
                .isAsync(false)
                .build());

        register(Tool.builder()
                .id("calculator")
                .name("Calculator")
                .description("Perform calculations")
                .category("utility")
                .parameters(Map.of(
                        "expression", Tool.ToolParameter.builder()
                                .name("expression")
                                .type("string")
                                .description("Mathematical expression")
                                .required(true)
                                .build()
                ))
                .handler(params -> "Mock calculation result")
                .isAsync(false)
                .build());

        register(Tool.builder()
                .id("time")
                .name("Current Time")
                .description("Get the current time")
                .category("utility")
                .parameters(Map.of())
                .handler(params -> Instant.now().toString())
                .isAsync(false)
                .build());

        log.info("Registered {} built-in tools", tools.size());
    }

    /**
     * 注册工具
     * @param tool 工具对象
     */
    public void register(Tool tool) {
        tools.put(tool.getId(), tool);
        log.info("Registered tool: {}", tool.getId());
    }

    /**
     * 注销工具
     * @param toolId 工具ID
     * @return 是否成功注销
     */
    public boolean unregister(String toolId) {
        return tools.remove(toolId) != null;
    }

    /**
     * 获取工具
     * @param toolId 工具ID
     * @return 工具对象
     */
    public Tool getTool(String toolId) {
        return tools.get(toolId);
    }

    /**
     * 获取所有工具列表
     * @return 工具列表
     */
    public List<Tool> getAllTools() {
        return List.copyOf(tools.values());
    }

    /**
     * 执行工具
     * @param toolId 工具ID
     * @param parameters 参数
     * @return 执行结果
     */
    public Object execute(String toolId, Map<String, Object> parameters) {
        Tool tool = tools.get(toolId);
        if (tool == null) {
            throw new IllegalArgumentException("Tool not found: " + toolId);
        }
        return tool.getHandler().apply(parameters);
    }

    /**
     * 获取所有工具的技能信息
     * @return 技能信息列表
     */
    public List<SkillInfo> getAllSkillInfos() {
        return tools.values().stream()
                .map(tool -> SkillInfo.builder()
                        .id(tool.getId())
                        .name(tool.getName())
                        .description(tool.getDescription())
                        .category(tool.getCategory())
                        .isAsync(tool.isAsync())
                        .parameters(tool.getParameters().values().stream()
                                .map(p -> SkillInfo.ParameterInfo.builder()
                                        .name(p.getName())
                                        .type(p.getType())
                                        .description(p.getDescription())
                                        .required(p.isRequired())
                                        .defaultValue(p.getDefaultValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取指定工具的技能信息
     * @param skillId 技能ID
     * @return 技能信息
     */
    public SkillInfo getSkillInfo(String skillId) {
        Tool tool = tools.get(skillId);
        if (tool == null) {
            return null;
        }
        return SkillInfo.builder()
                .id(tool.getId())
                .name(tool.getName())
                .description(tool.getDescription())
                .category(tool.getCategory())
                .isAsync(tool.isAsync())
                .parameters(tool.getParameters().values().stream()
                        .map(p -> SkillInfo.ParameterInfo.builder()
                                .name(p.getName())
                                .type(p.getType())
                                .description(p.getDescription())
                                .required(p.isRequired())
                                .defaultValue(p.getDefaultValue())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
