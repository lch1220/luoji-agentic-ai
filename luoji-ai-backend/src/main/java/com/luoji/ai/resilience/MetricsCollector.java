/*
 * MetricsCollector - 指标收集器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 指标收集器，收集和聚合系统各类指标，包括请求统计、
 *              熔断器状态、重试统计和系统资源使用情况
 */
package com.luoji.ai.resilience;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsCollector {

    private final CircuitBreakerManager circuitBreakerManager;
    private final RetryManager retryManager;
    private final RateLimiterManager rateLimiter;

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);

    /**
     * 记录请求
     */
    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    /**
     * 记录成功
     */
    public void recordSuccess() {
        successfulRequests.incrementAndGet();
    }

    /**
     * 记录失败
     */
    public void recordFailure() {
        failedRequests.incrementAndGet();
    }

    /**
     * 收集所有指标
     * @return 指标映射
     */
    public Map<String, Object> collectAllMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Request metrics
        Map<String, Object> requestMetrics = new HashMap<>();
        requestMetrics.put("total", totalRequests.get());
        requestMetrics.put("successful", successfulRequests.get());
        requestMetrics.put("failed", failedRequests.get());
        requestMetrics.put("successRate", calculateSuccessRate());
        metrics.put("requests", requestMetrics);

        // Circuit breaker metrics
        metrics.put("circuitBreakers", circuitBreakerManager.getAllMetrics());

        // Retry metrics
        metrics.put("retries", retryManager.getAllMetrics());

        // System metrics
        metrics.put("system", collectSystemMetrics());

        return metrics;
    }

    /**
     * 计算成功率
     * @return 成功率百分比
     */
    private double calculateSuccessRate() {
        long total = totalRequests.get();
        if (total == 0) return 0.0;
        return (double) successfulRequests.get() / total * 100;
    }

    /**
     * 收集系统指标
     * 包括内存和线程信息
     * @return 系统指标映射
     */
    private Map<String, Object> collectSystemMetrics() {
        Map<String, Object> systemMetrics = new HashMap<>();

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        systemMetrics.put("heapUsed", memoryBean.getHeapMemoryUsage().getUsed());
        systemMetrics.put("heapMax", memoryBean.getHeapMemoryUsage().getMax());
        systemMetrics.put("heapCommitted", memoryBean.getHeapMemoryUsage().getCommitted());

        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        systemMetrics.put("threadCount", threadBean.getThreadCount());
        systemMetrics.put("peakThreadCount", threadBean.getPeakThreadCount());

        return systemMetrics;
    }

    /**
     * 获取健康状态
     * @return 健康状态映射
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("totalRequests", totalRequests.get());
        health.put("successRate", calculateSuccessRate());

        Map<String, Object> circuitBreakerHealth = new HashMap<>();
        circuitBreakerManager.getAllMetrics().forEach((name, metrics) -> {
            circuitBreakerHealth.put(name, Map.of(
                    "state", metrics.getState(),
                    "failureRate", metrics.getFailureRate()
            ));
        });
        health.put("circuitBreakers", circuitBreakerHealth);

        return health;
    }
}
