/*
 * RetryManager - 重试管理器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 重试管理器，基于Resilience4j实现，
 *              提供重试器的获取和重试统计指标收集功能
 */
package com.luoji.ai.resilience;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryManager {

    private final RetryRegistry registry;
    private final Map<String, RetryMetrics> metricsMap = new HashMap<>();

    /**
     * 获取重试器
     * @param name 重试器名称
     * @return 重试器实例
     */
    public Retry getRetry(String name) {
        return registry.retry(name);
    }

    /**
     * 获取重试器指标
     * @param name 重试器名称
     * @return 重试指标
     */
    public RetryMetrics getMetrics(String name) {
        Retry retry = getRetry(name);
        RetryMetrics metrics = new RetryMetrics();
        metrics.setName(name);
        metrics.setNumberOfSuccessfulCallsWithRetry(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
        metrics.setNumberOfFailedCallsWithRetry(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt());

        metricsMap.put(name, metrics);
        return metrics;
    }

    /**
     * 获取所有重试器指标
     * @return 重试指标映射
     */
    public Map<String, RetryMetrics> getAllMetrics() {
        registry.getAllRetries().forEach(retry -> getMetrics(retry.getName()));
        return new HashMap<>(metricsMap);
    }

    /**
     * 重试指标内部类
     */
    @lombok.Data
    public static class RetryMetrics {
        private String name;
        private long numberOfSuccessfulCallsWithRetry;
        private long numberOfFailedCallsWithRetry;
    }
}
