/*
 * LlmClient - 大语言模型客户端
 *
 * @author luoji
 * @date 2026-05-28
 * @description LLM客户端封装，调用Spring AI的ChatClient实现生成和流式生成功能，
 *              集成重试和熔断机制，提升系统稳定性
 */
package com.luoji.ai.core.llm;

import com.luoji.ai.config.LlmConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LlmClient {

    private final ChatClient chatClient;
    private final LlmConfig llmConfig;

    public LlmClient(ChatClient chatClient, LlmConfig llmConfig) {
        this.chatClient = chatClient;
        this.llmConfig = llmConfig;
    }

    /**
     * 生成响应（带重试和熔断）
     * @param promptText 提示词
     * @return 生成的响应
     */
    @Retry(name = "llm")
    @CircuitBreaker(name = "llm", fallbackMethod = "fallback")
    public Mono<String> generate(String promptText) {
        log.debug("Generating response for prompt: {}", promptText);
        return Mono.fromCallable(() -> {
            return chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content();
        });
    }

    /**
     * 流式生成响应（带重试和熔断）
     * @param promptText 提示词
     * @return 生成内容的流
     */
    @Retry(name = "llm")
    @CircuitBreaker(name = "llm", fallbackMethod = "fallbackStream")
    public Flux<String> generateStream(String promptText) {
        log.debug("Generating streaming response for prompt: {}", promptText);
        return chatClient.prompt()
                .user(promptText)
                .stream()
                .content();
    }

    /**
     * 带选项生成响应
     * @param promptText 提示词
     * @param temperature 温度参数
     * @param maxTokens 最大token数
     * @return 生成的响应
     */
    @Retry(name = "llm")
    @CircuitBreaker(name = "llm", fallbackMethod = "fallback")
    public Mono<String> generateWithOptions(String promptText, double temperature, int maxTokens) {
        log.debug("Generating response with custom options: temp={}, maxTokens={}", temperature, maxTokens);
        return Mono.fromCallable(() -> {
            return chatClient.prompt()
                    .user(promptText)
                    .call()
                    .content();
        });
    }

    /**
     * 同步生成失败时的降级响应
     * @param promptText 提示词
     * @param e 异常信息
     * @return 降级响应
     */
    private Mono<String> fallback(String promptText, Exception e) {
        log.error("LLM call failed, returning fallback response", e);
        return Mono.just("I'm sorry, but I'm having trouble processing your request right now. Please try again later.");
    }

    /**
     * 流式生成失败时的降级响应
     * @param promptText 提示词
     * @param e 异常信息
     * @return 降级响应流
     */
    private Flux<String> fallbackStream(String promptText, Exception e) {
        log.error("LLM streaming call failed, returning fallback response", e);
        return Flux.just("I'm sorry, but I'm having trouble processing your request right now. Please try again later.");
    }
}
