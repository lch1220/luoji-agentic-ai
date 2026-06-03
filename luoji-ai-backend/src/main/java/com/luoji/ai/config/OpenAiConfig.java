/*
 * OpenAiConfig - OpenAI配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description OpenAI API配置类，负责创建OpenAiApi、OpenAiChatModel和ChatClient Bean，
 *              提供与OpenAI服务通信的基础组件
 */
package com.luoji.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenAiConfig {

    @Autowired
    private LlmConfig llmConfig;

    /**
     * 创建OpenAiApi Bean
     * 使用LlmConfig中的配置初始化OpenAI API客户端
     * @return OpenAiApi实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(
                llmConfig.getOpenai().getBaseUrl(),
                llmConfig.getOpenai().getApiKey()
        );
    }

    /**
     * 创建OpenAiChatModel Bean
     * @param openAiApi OpenAI API实例
     * @return OpenAiChatModel实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public OpenAiChatModel chatModel(OpenAiApi openAiApi) {
        return new OpenAiChatModel(openAiApi);
    }

    /**
     * 创建ChatClient Bean
     * 使用建造者模式创建ChatClient
     * @param chatModel OpenAI聊天模型
     * @return ChatClient实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        // 使用建造者模式创建ChatClient
        return ChatClient.builder(chatModel).build();
    }
}
