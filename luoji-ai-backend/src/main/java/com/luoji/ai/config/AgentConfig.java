/*
 * AgentConfig - AI智能体配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description 配置管理类，用于配置不同类型Agent（FastQA、Knowledge、Planning）的参数，
 *              包括模型选择、上下文长度限制、重试策略、流式输出等配置项
 */
package com.luoji.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "agent")
public class AgentConfig {

    // FastQA智能体配置 - 用于快速问答场景
    private FastQa fastQa = new FastQa();
    // 知识库智能体配置 - 用于RAG检索增强生成
    private Knowledge knowledge = new Knowledge();
    // 规划智能体配置 - 用于复杂任务分解和执行
    private Planning planning = new Planning();

    /**
     * FastQA智能体配置类
     * 用于配置快速问答场景下的模型参数和行为设置
     */
    @Data
    public static class FastQa {
        private String defaultModel = "gpt-4o";  // 默认使用的模型
        private int maxContextLength = 32000;      // 最大上下文长度
        private int maxRetries = 3;               // 最大重试次数
        private boolean enableStreaming = true;    // 是否启用流式输出
    }

    /**
     * 知识库智能体配置类
     * 用于配置RAG（检索增强生成）场景下的参数
     */
    @Data
    public static class Knowledge {
        private String defaultModel = "gpt-4o";       // 默认模型
        private String vectorStore = "milvus";         // 向量数据库类型
        private String embeddingModel = "text-embedding-3-small";  // 嵌入模型
        private int topK = 5;                           // 检索返回的最相似文档数量
        private double similarityThreshold = 0.7;      // 相似度阈值，低于此值的结果会被过滤
    }

    /**
     * 规划智能体配置类
     * 用于配置复杂任务规划和执行场景的参数
     */
    @Data
    public static class Planning {
        private String defaultModel = "gpt-4o";        // 默认模型
        private int maxSteps = 10;                      // 最大执行步骤数
        private int planTimeoutSeconds = 300;           // 规划超时时间（秒）
        private boolean enableCoT = true;              // 是否启用链式思考(Chain of Thought)
        private boolean enableToT = false;             // 是否启用思维树(Tree of Thoughts)
    }
}
