/*
 * 洛极AI智能体框架 - 核心应用类
 *
 * @author luoji
 * @date 2026-05-28
 * @description 洛极AI智能体框架的Spring Boot应用主入口，负责启动整个后端服务
 */
package com.luoji.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.luoji.ai.config.LlmConfig;
import com.luoji.ai.config.AgentConfig;
import com.luoji.ai.config.SecurityProperties;

/**
 * Spring Boot应用主类
 * 启用配置属性绑定，自动扫描并加载LlmConfig、AgentConfig、SecurityProperties等配置类
 */
@SpringBootApplication
@EnableConfigurationProperties({LlmConfig.class, AgentConfig.class, SecurityProperties.class})
public class LuoJiAiApplication {

    /**
     * 应用入口方法
     * 启动Spring Boot应用，运行整个AI智能体框架后端服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(LuoJiAiApplication.class, args);
    }
}
