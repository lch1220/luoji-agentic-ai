/*
 * LlmConfig - 大语言模型配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description LLM配置管理类，用于配置OpenAI及其他模型的API密钥、基础URL、
 *              模型参数（温度、最大token数等），支持多模型管理
 */
package com.luoji.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.ai")
public class LlmConfig {

    // OpenAI配置
    private OpenAi openai = new OpenAi();
    // 多模型配置映射
    private Map<String, ModelConfig> models;

    /**
     * OpenAI配置类
     * 包含API密钥、基础URL和聊天选项配置
     */
    @Data
    public static class OpenAi {
        private String apiKey;              // API密钥
        private String baseUrl;            // API基础URL
        private Chat chat = new Chat();    // 聊天配置

        @Data
        public static class Chat {
            private Options options = new Options();
        }

        /**
         * 聊天选项配置类
         * 定义模型、温度、最大token等参数
         */
        @Data
        public static class Options {
            private String model = "gpt-4o";     // 默认模型
            private double temperature = 0.7;    // 生成温度
            private int maxTokens = 4096;       // 最大token数
        }
    }

    /**
     * 模型配置类
     * 支持配置不同提供商的模型参数
     */
    @Data
    public static class ModelConfig {
        private String name;          // 模型名称
        private String apiKey;       // API密钥
        private String baseUrl;      // 基础URL
        private double temperature;  // 温度参数
        private int maxTokens;      // 最大token数
        private String provider;     // 提供商
    }

    /**
     * 根据模型名称获取模型配置
     * 如果未找到对应配置，返回默认空配置
     * @param modelName 模型名称
     * @return 模型配置
     */
    public ModelConfig getModelConfig(String modelName) {
        if (models != null && models.containsKey(modelName)) {
            return models.get(modelName);
        }
        return new ModelConfig();
    }
}
