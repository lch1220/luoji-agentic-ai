/*
 * MemoryConfig - 记忆管理配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description 记忆系统配置类，用于配置对话记忆的存储策略、
 *              向量记忆功能以及不同存储后端的参数
 */
package com.luoji.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "memory")
public class MemoryConfig {

    private int maxConversationLength = 100;     // 最大对话轮数
    private int maxMessageAgeHours = 24;         // 消息最大保留时间（小时）
    private boolean enableVectorMemory = true;   // 是否启用向量记忆
    private String vectorStoreType = "redis";   // 向量存储类型
    private int summaryThreshold = 50;          // 触发摘要的阈值

    /**
     * Redis记忆存储配置类
     * 用于配置Redis作为记忆存储后端的参数
     */
    @Data
    public static class RedisMemory {
        private String host = "localhost";           // Redis主机地址
        private int port = 6379;                     // Redis端口
        private String prefix = "luoji:memory:";    // 键名前缀
        private int ttlSeconds = 86400;             // 数据过期时间（秒）
    }

    /**
     * MongoDB记忆存储配置类
     * 用于配置MongoDB作为记忆存储后端的参数
     */
    @Data
    public static class MongoMemory {
        private String collection = "conversations"; // 集合名称
        private int maxDocuments = 1000;             // 最大文档数
    }
}
