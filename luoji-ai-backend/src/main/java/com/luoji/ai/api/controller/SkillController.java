/*
 * SkillController - 技能控制器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 技能管理API控制器，提供技能的注册、注销、查询和执行功能，
 *              支持动态管理和调用各种AI技能
 */
package com.luoji.ai.api.controller;

import com.luoji.ai.api.dto.SkillExecutionRequest;
import com.luoji.ai.api.dto.SkillExecutionResponse;
import com.luoji.ai.api.dto.SkillInfo;
import com.luoji.ai.tools.SkillRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill management and execution API")
public class SkillController {

    private final SkillRegistry skillRegistry;

    /**
     * 获取所有可用技能列表
     * @return 技能信息列表
     */
    @GetMapping
    @Operation(summary = "List all available skills")
    public Mono<List<SkillInfo>> listSkills() {
        return Mono.just(skillRegistry.getAllSkillInfos());
    }

    /**
     * 获取指定技能的详细信息
     * @param skillId 技能ID
     * @return 技能信息
     */
    @GetMapping("/{skillId}")
    @Operation(summary = "Get skill details")
    public Mono<SkillInfo> getSkill(@PathVariable String skillId) {
        return Mono.justOrEmpty(skillRegistry.getSkillInfo(skillId));
    }

    /**
     * 执行指定技能
     * @param skillId 技能ID
     * @param request 执行请求（包含参数）
     * @return 执行结果
     */
    @PostMapping("/execute/{skillId}")
    @Operation(summary = "Execute a skill")
    public Mono<SkillExecutionResponse> executeSkill(
            @PathVariable String skillId,
            @RequestBody SkillExecutionRequest request) {
        log.info("Executing skill: {}", skillId);
        return skillRegistry.execute(skillId, request.getParameters());
    }

    /**
     * 注册新技能
     * @param skillInfo 技能信息
     * @return 注册是否成功
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new skill")
    public Mono<Boolean> registerSkill(@RequestBody SkillInfo skillInfo) {
        log.info("Registering skill: {}", skillInfo.getId());
        skillRegistry.register(skillInfo);
        return Mono.just(true);
    }

    /**
     * 注销技能
     * @param skillId 技能ID
     * @return 注销是否成功
     */
    @DeleteMapping("/{skillId}")
    @Operation(summary = "Unregister a skill")
    public Mono<Boolean> unregisterSkill(@PathVariable String skillId) {
        log.info("Unregistering skill: {}", skillId);
        return Mono.just(skillRegistry.unregister(skillId));
    }
}
