/*
 * ResilienceConfig - 弹性容错配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description 弹性容错配置类，初始化和管理CircuitBreaker（熔断器）和Retry（重试）机制，
 *              提供系统应对故障的能力，提升系统稳定性
 */
package com.luoji.ai.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
public class ResilienceConfig {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    /**
     * 初始化弹性配置
     * 在Bean创建后打印当前可用的熔断器和重试器数量
     */
    @PostConstruct
    public void init() {
        log.info("Resilience configuration initialized");
        log.info("Circuit breakers available: {}", circuitBreakerRegistry.getAllCircuitBreakers().size());
        log.info("Retries available: {}", retryRegistry.getAllRetries().size());
    }

    /**
     * 获取指定名称的CircuitBreaker实例
     * @param name 熔断器名称
     * @return CircuitBreaker实例
     */
    public CircuitBreaker getCircuitBreaker(String name) {
        return circuitBreakerRegistry.circuitBreaker(name);
    }

    /**
     * 获取指定名称的Retry实例
     * @param name 重试器名称
     * @return Retry实例
     */
    public Retry getRetry(String name) {
        return retryRegistry.retry(name);
    }
}
