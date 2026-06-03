/*
 * McpConfig - MCP（Model Context Protocol）配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description MCP协议配置类，用于配置MCP服务器连接信息，
 *              支持多服务器管理和请求头配置、超时设置等
 */
package com.luoji.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "mcp")
public class McpConfig {

    // 是否启用MCP协议支持
    private boolean enabled = false;
    // MCP服务器映射，key为服务器标识符
    private Map<String, ServerConfig> servers;

    /**
     * MCP服务器配置类
     * 定义单个MCP服务器的连接参数
     */
    @Data
    public static class ServerConfig {
        private String url;                          // 服务器地址
        private String name;                          // 服务器名称
        private Map<String, String> headers;          // 自定义请求头
        private int timeoutSeconds = 30;              // 请求超时时间（秒）
        private int maxConnections = 10;              // 最大连接数
    }
}
