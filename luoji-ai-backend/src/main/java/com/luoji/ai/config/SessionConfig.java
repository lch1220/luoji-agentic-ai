/*
 * SessionConfig - 会话管理配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description 会话管理配置类，定义会话的最大数量、超时时间、历史记录限制等参数，
 *              以及会话状态枚举
 */
package com.luoji.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "session")
public class SessionConfig {

    private int maxSessions = 1000;               // 最大并发会话数
    private long sessionTimeoutMinutes = 60;     // 会话超时时间（分钟）
    private int maxHistoryPerSession = 100;       // 每个会话最大历史记录数

    /**
     * 会话状态枚举
     * 定义会话的完整生命周期状态
     */
    public enum SessionState {
        INIT,              // 初始化状态
        ACTIVE,            // 活跃状态
        WAITING_FOR_INPUT, // 等待输入
        PAUSED,            // 暂停状态
        COMPLETED,         // 已完成
        ERROR              // 错误状态
    }
}
