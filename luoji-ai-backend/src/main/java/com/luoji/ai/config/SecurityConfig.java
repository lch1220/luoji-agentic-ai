/*
 * SecurityConfig - JWT安全配置与工具类
 *
 * @author luoji
 * @date 2026-05-28
 * @description JWT令牌生成、验证和解析的核心组件，
 *              提供用户认证、令牌验证、权限提取等功能
 */
package com.luoji.ai.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class SecurityConfig {

    @Autowired
    private SecurityProperties securityProperties;

    private SecretKey secretKey;

    /**
     * 初始化JWT签名密钥
     * 使用配置的密钥创建HMAC签名密钥
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(
            securityProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 生成JWT令牌
     * @param username 用户名
     * @param roles 用户角色列表
     * @return JWT令牌字符串
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        // 计算过期时间
        Date expiration = new Date(now.getTime() + securityProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取认证信息
     * @param token JWT令牌
     * @return Authentication认证对象
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        // 提取角色列表
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        // 转换为Spring Security权限对象
        List<SimpleGrantedAuthority> authorities = roles != null
                ? roles.stream().map(SimpleGrantedAuthority::new).toList()
                : Collections.emptyList();

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    /**
     * 获取令牌的完整声明
     * @param token JWT令牌
     * @return Claims声明对象
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
