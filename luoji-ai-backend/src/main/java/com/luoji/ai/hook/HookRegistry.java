/*
 * HookRegistry - 钩子注册表
 *
 * @author luoji
 * @date 2026-05-28
 * @description 钩子注册表，管理所有AI系统钩子，
 *              提供钩子的注册、触发和执行功能
 */
package com.luoji.ai.hook;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HookRegistry {

    // 钩子存储映射
    private final Map<String, Hook> hooks = new ConcurrentHashMap<>();

    /**
     * 初始化钩子注册表
     * 注册内置钩子
     */
    @PostConstruct
    public void init() {
        registerBuiltInHooks();
    }

    /**
     * 注册内置钩子
     * 包括日志钩子用于记录后处理操作
     */
    private void registerBuiltInHooks() {
        register(Hook.builder()
                .id("logging_hook")
                .name("Logging Hook")
                .type(Hook.HookType.POST_PROCESS)
                .priority(100)
                .enabled(true)
                .handler(ctx -> {
                    log.info("Hook executed: {} for session {}", ctx.getHookType(), ctx.getSessionId());
                    return HookResult.builder().success(true).build();
                })
                .build());

        log.info("Registered {} built-in hooks", hooks.size());
    }

    /**
     * 注册钩子
     * @param hook 钩子对象
     */
    public void register(Hook hook) {
        hooks.put(hook.getId(), hook);
        log.info("Registered hook: {} of type {}", hook.getId(), hook.getType());
    }

    /**
     * 注销钩子
     * @param hookId 钩子ID
     * @return 是否成功注销
     */
    public boolean unregister(String hookId) {
        return hooks.remove(hookId) != null;
    }

    /**
     * 获取钩子
     * @param hookId 钩子ID
     * @return 钩子对象
     */
    public Hook getHook(String hookId) {
        return hooks.get(hookId);
    }

    /**
     * 根据类型获取钩子列表
     * 按优先级降序排列
     * @param type 钩子类型
     * @return 钩子列表
     */
    public List<Hook> getHooksByType(Hook.HookType type) {
        return hooks.values().stream()
                .filter(h -> h.getType() == type && h.isEnabled())
                .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                .collect(Collectors.toList());
    }

    /**
     * 执行钩子
     * 按优先级顺序执行所有适用的钩子
     * @param context 钩子上下文
     * @return 执行结果
     */
    public HookResult execute(HookContext context) {
        List<Hook> applicableHooks = getHooksByType(context.getHookType());

        HookResult result = HookResult.builder().success(true).build();

        for (Hook hook : applicableHooks) {
            try {
                HookResult hookResult = hook.getHandler().apply(context);
                if (!hookResult.isSuccess()) {
                    log.warn("Hook {} failed: {}", hook.getId(), hookResult.getMessage());
                    return hookResult;
                }
                if (hookResult.isModified()) {
                    result = hookResult;
                }
            } catch (Exception e) {
                log.error("Error executing hook {}: {}", hook.getId(), e.getMessage());
                return HookResult.builder()
                        .success(false)
                        .message("Hook execution failed: " + e.getMessage())
                        .build();
            }
        }

        return result;
    }

    /**
     * 执行指定类型的所有钩子
     * @param type 钩子类型
     * @param context 上下文
     * @return 执行结果
     */
    public HookResult executeHooks(Hook.HookType type, HookContext context) {
        context.setHookType(type);
        return execute(context);
    }
}
