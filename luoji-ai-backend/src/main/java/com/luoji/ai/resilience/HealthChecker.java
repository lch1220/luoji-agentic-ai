/*
 * HealthChecker - 健康检查器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 健康检查器，提供系统健康状态检查、Kubernetes就绪探针和存活探针接口，
 *              用于监控系统的可用性和依赖服务状态
 */
package com.luoji.ai.resilience;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthChecker {

    private final MetricsCollector metricsCollector;
    private final CircuitBreakerManager circuitBreakerManager;

    /**
     * 健康检查
     * 返回系统整体健康状态和详细信息
     * @return 健康状态映射
     */
    public Mono<Map<String, Object>> checkHealth() {
        return Mono.fromCallable(() -> {
            Map<String, Object> health = new HashMap<>();
            health.put("status", determineOverallStatus());
            health.put("details", metricsCollector.getHealthStatus());
            health.put("timestamp", System.currentTimeMillis());
            return health;
        }).timeout(Duration.ofSeconds(5));
    }

    /**
     * 存活探针
     * 用于Kubernetes检查容器是否存活
     * @return 存活状态
     */
    public Mono<Map<String, Object>> checkLiveness() {
        return Mono.just(Map.of(
                "status", "UP",
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * 就绪探针
     * 用于Kubernetes检查容器是否准备好接收流量
     * @return 就绪状态
     */
    public Mono<Map<String, Object>> checkReadiness() {
        return Mono.fromCallable(() -> {
            Map<String, Object> readiness = new HashMap<>();
            boolean isReady = isReady();
            readiness.put("ready", isReady);
            readiness.put("status", isReady ? "UP" : "DOWN");
            readiness.put("timestamp", System.currentTimeMillis());
            return readiness;
        }).timeout(Duration.ofSeconds(5));
    }

    /**
     * 确定整体健康状态
     * @return 状态字符串
     */
    private String determineOverallStatus() {
        Map<String, Object> healthStatus = metricsCollector.getHealthStatus();
        if (healthStatus.containsKey("status")) {
            return healthStatus.get("status").toString();
        }
        return "UNKNOWN";
    }

    /**
     * 检查系统是否就绪
     * 通过检查所有熔断器状态判断
     * @return 是否就绪
     */
    private boolean isReady() {
        // Check if all circuit breakers are not in OPEN state
        // 检查所有熔断器是否不在OPEN状态
        Map<String, CircuitBreakerManager.CircuitBreakerMetrics> cbMetrics =
                circuitBreakerManager.getAllMetrics();

        for (CircuitBreakerManager.CircuitBreakerMetrics metrics : cbMetrics.values()) {
            if ("OPEN".equals(metrics.getState())) {
                log.warn("Circuit breaker {} is OPEN", metrics.getName());
                return false;
            }
        }
        return true;
    }
}
