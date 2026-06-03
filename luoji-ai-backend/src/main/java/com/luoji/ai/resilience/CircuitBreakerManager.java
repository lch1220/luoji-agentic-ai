/*
 * CircuitBreakerManager - 熔断器管理器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 熔断器管理器，基于Resilience4j实现，
 *              提供熔断器的获取、装饰执行和指标收集功能
 */
package com.luoji.ai.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerManager {

    private final CircuitBreakerRegistry registry;
    private final Map<String, CircuitBreakerMetrics> metricsMap = new HashMap<>();

    /**
     * 获取熔断器
     * @param name 熔断器名称
     * @return 熔断器实例
     */
    public CircuitBreaker getCircuitBreaker(String name) {
        return registry.circuitBreaker(name);
    }

    /**
     * 装饰Supplier
     * 将Supplier与熔断器关联
     * @param circuitBreakerName 熔断器名称
     * @param supplier 待装饰的Supplier
     * @return 装饰后的Supplier
     */
    public <T> Supplier<T> decorateSupplier(String circuitBreakerName, Supplier<T> supplier) {
        CircuitBreaker cb = getCircuitBreaker(circuitBreakerName);
        return CircuitBreaker.decorateSupplier(cb, supplier);
    }

    /**
     * 执行Supplier（带熔断保护）
     * @param circuitBreakerName 熔断器名称
     * @param supplier 待执行的Supplier
     * @return 执行结果
     */
    public <T> T executeSupplier(String circuitBreakerName, Supplier<T> supplier) {
        return getCircuitBreaker(circuitBreakerName).executeSupplier(supplier);
    }

    /**
     * 获取熔断器指标
     * @param name 熔断器名称
     * @return 熔断器指标
     */
    public CircuitBreakerMetrics getMetrics(String name) {
        CircuitBreaker cb = getCircuitBreaker(name);
        CircuitBreakerMetrics metrics = new CircuitBreakerMetrics();
        metrics.setName(name);
        metrics.setState(cb.getState().toString());
        metrics.setFailureRate(cb.getMetrics().getFailureRate());
        metrics.setNumberOfSuccessfulCalls(cb.getMetrics().getNumberOfSuccessfulCalls());
        metrics.setNumberOfFailedCalls(cb.getMetrics().getNumberOfFailedCalls());
        metrics.setNumberOfNotPermittedCalls(cb.getMetrics().getNumberOfNotPermittedCalls());

        metricsMap.put(name, metrics);
        return metrics;
    }

    /**
     * 获取所有熔断器指标
     * @return 熔断器指标映射
     */
    public Map<String, CircuitBreakerMetrics> getAllMetrics() {
        registry.getAllCircuitBreakers().forEach(cb -> getMetrics(cb.getName()));
        return new HashMap<>(metricsMap);
    }

    /**
     * 重置熔断器
     * @param name 熔断器名称
     */
    public void resetCircuitBreaker(String name) {
        getCircuitBreaker(name).reset();
        log.info("Reset circuit breaker: {}", name);
    }

    /**
     * 熔断器指标内部类
     */
    @lombok.Data
    public static class CircuitBreakerMetrics {
        private String name;
        private String state;
        private float failureRate;
        private long numberOfSuccessfulCalls;
        private long numberOfFailedCalls;
        private long numberOfNotPermittedCalls;
    }
}
