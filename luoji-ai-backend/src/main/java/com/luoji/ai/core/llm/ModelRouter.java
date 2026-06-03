/*
 * ModelRouter - 模型路由
 *
 * @author luoji
 * @date 2026-05-28
 * @description 模型路由器，根据任务类型选择合适的LLM模型，
 *              管理多个模型的客户端实例
 */
package com.luoji.ai.core.llm;

import com.luoji.ai.config.LlmConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModelRouter {

    private final LlmConfig llmConfig;
    // 模型客户端映射表
    private final Map<String, LlmClient> modelClients = new ConcurrentHashMap<>();

    /**
     * 获取默认模型名称
     * @return 默认模型名称
     */
    public String getDefaultModel() {
        return llmConfig.getOpenai().getChat().getOptions().getModel();
    }

    /**
     * 根据模型名称获取LLM客户端
     * @param modelName 模型名称
     * @return LLM客户端实例
     */
    public LlmClient getClient(String modelName) {
        if (modelClients.containsKey(modelName)) {
            return modelClients.get(modelName);
        }

        LlmConfig.ModelConfig config = llmConfig.getModelConfig(modelName);
        log.info("Creating LLM client for model: {} with provider: {}", modelName, config.getProvider());

        // Return the default client for now - in production, create provider-specific clients
        // 目前返回默认客户端，生产环境应根据提供商创建特定客户端
        return modelClients.get(getDefaultModel());
    }

    /**
     * 根据任务类型选择合适的模型
     * @param taskType 任务类型（fast_qa、reasoning、code、creative等）
     * @return 选择的模型名称
     */
    public String selectModel(String taskType) {
        return switch (taskType) {
            case "fast_qa" -> getDefaultModel();
            case "reasoning" -> "gpt-4o";
            case "code" -> "gpt-4o";
            case "creative" -> "gpt-4o";
            default -> getDefaultModel();
        };
    }

    /**
     * 注册模型客户端
     * @param modelName 模型名称
     * @param client LLM客户端
     */
    public void registerModelClient(String modelName, LlmClient client) {
        modelClients.put(modelName, client);
        log.info("Registered LLM client for model: {}", modelName);
    }
}
