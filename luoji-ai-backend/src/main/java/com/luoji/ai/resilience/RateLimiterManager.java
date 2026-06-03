/*
 * RateLimiterManager - 限流器管理器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 限流器管理器，基于Resilience4j实现，
 *              提供限流器的获取、权限获取和带限流的执行功能
 */
package com.luoji.ai.resilience;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterManager {

    private final RateLimiterRegistry registry;
    private final Map<String, RateLimiterMetrics> metricsMap = new ConcurrentHashMap<>();

    /**
     * 获取限流器
     * @param name 限流器名称
     * @return 限流器实例
     */
    public RateLimiter getRateLimiter(String name) {
        return registry.rateLimiter(name);
    }

    /**
     * 尝试获取执行权限
     * @param name 限流器名称
     * @return 是否获取成功
     */
    public boolean tryAcquire(String name) {
        RateLimiter rl = getRateLimiter(name);
        boolean acquired = rl.acquirePermission();
        updateMetrics(name, acquired);
        return acquired;
    }

    /**
     * 带限流的执行Supplier
     * @param name 限流器名称
     * @param supplier 待执行的Supplier
     * @return 执行结果
     */
    public <T> T execute(String name, java.util.function.Supplier<T> supplier) {
        RateLimiter rl = getRateLimiter(name);
        return rl.executeSupplier(supplier);
    }

    /**
     * 带限流的执行Runnable
     * @param name 限流器名称
     * @param runnable 待执行的Runnable
     */
    public void execute(String name, Runnable runnable) {
        RateLimiter rl = getRateLimiter(name);
        rl.executeRunnable(runnable);
    }

    /**
     * 更新指标
     * @param name 限流器名称
     * @param acquired 是否获取成功
     */
    private void updateMetrics(String name, boolean acquired) {
        metricsMap.computeIfAbsent(name, k -> new RateLimiterMetrics()).recordAttempt(acquired);
    }

    /**
     * 获取限流器指标
     * @param name 限流器名称
     * @return 限流器指标
     */
    public RateLimiterMetrics getMetrics(String name) {
        return metricsMap.getOrDefault(name, new RateLimiterMetrics());
    }

    /**
     * 限流器指标内部类
     */
    @lombok.Data
    public static class RateLimiterMetrics {
        private String name;
        private final AtomicInteger totalAttempts = new AtomicInteger(0);
        private final AtomicInteger successfulAttempts = new AtomicInteger(0);

        /**
         * 记录一次尝试
         * @param acquired 是否获取成功
         */
        public void recordAttempt(boolean acquired) {
            totalAttempts.incrementAndGet();
            if (acquired) {
                successfulAttempts.incrementAndGet();
            }
        }

        public int getTotalAttempts() { return totalAttempts.get(); }
        public int getSuccessfulAttempts() { return successfulAttempts.get(); }
        public int getRejectedAttempts() { return totalAttempts.get() - successfulAttempts.get(); }
    }
}
