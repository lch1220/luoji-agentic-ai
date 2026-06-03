/*
 * SecurityProperties - JWT安全配置类
 *
 * @author luoji
 * @date 2026-05-28
 * @description JWT令牌配置类，负责配置JWT签名密钥和令牌过期时间，
 *              用于用户认证和授权管理
 */
package com.luoji.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    // JWT签名密钥 - 生产环境需要更换为强密钥
    private String secret = "default-secret-key-change-in-production";
    // 令牌过期时间（毫秒），默认24小时
    private long expiration = 86400000;

    /**
     * 获取JWT签名密钥
     * @return 签名密钥字符串
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 获取令牌过期时间
     * @return 过期时间（毫秒）
     */
    public long getExpiration() {
        return expiration;
    }
}
