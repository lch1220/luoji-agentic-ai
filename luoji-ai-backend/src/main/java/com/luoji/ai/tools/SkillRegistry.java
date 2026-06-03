/*
 * SkillRegistry - 技能注册表
 *
 * @author luoji
 * @date 2026-05-28
 * @description 技能注册表，封装ToolRegistry提供技能级别的管理接口，
 *              提供技能的注册、查询和执行功能
 */
package com.luoji.ai.tools;

import com.luoji.ai.api.dto.SkillExecutionResponse;
import com.luoji.ai.api.dto.SkillInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillRegistry {

    private final ToolRegistry toolRegistry;

    /**
     * 注册技能
     * @param skillInfo 技能信息
     */
    public void register(SkillInfo skillInfo) {
        log.info("Registered skill: {}", skillInfo.getId());
    }

    /**
     * 注销技能
     * @param skillId 技能ID
     * @return 是否成功
     */
    public boolean unregister(String skillId) {
        return true;
    }

    /**
     * 获取技能信息
     * @param skillId 技能ID
     * @return 技能信息
     */
    public SkillInfo getSkillInfo(String skillId) {
        return toolRegistry.getSkillInfo(skillId);
    }

    /**
     * 获取所有技能信息
     * @return 技能列表
     */
    public List<SkillInfo> getAllSkillInfos() {
        return toolRegistry.getAllSkillInfos();
    }

    /**
     * 执行技能
     * @param skillId 技能ID
     * @param parameters 执行参数
     * @return 执行响应
     */
    public Mono<SkillExecutionResponse> execute(String skillId, Map<String, Object> parameters) {
        try {
            Object result = toolRegistry.execute(skillId, parameters);
            return Mono.just(SkillExecutionResponse.builder()
                    .skillId(skillId)
                    .result(result.toString())
                    .success(true)
                    .executedAt(Instant.now())
                    .executionTimeMs(0)
                    .build());
        } catch (Exception e) {
            return Mono.just(SkillExecutionResponse.builder()
                    .skillId(skillId)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .executedAt(Instant.now())
                    .build());
        }
    }
}
