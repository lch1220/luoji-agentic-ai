/*
 * AuthController - 认证控制器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 认证API控制器，提供用户登录、令牌验证和登出功能，
 *              使用JWT实现无状态认证
 */
package com.luoji.ai.api.controller;

import com.luoji.ai.api.dto.LoginRequest;
import com.luoji.ai.api.dto.LoginResponse;
import com.luoji.ai.config.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {

    private final SecurityConfig securityConfig;

    /**
     * 用户登录接口
     * 验证用户名密码，成功则返回JWT令牌
     * @param request 登录请求（包含用户名密码）
     * @return 登录响应（包含JWT令牌和用户信息）
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Mock authentication - in production, validate against database
        // 模拟认证 - 生产环境应连接数据库验证
        if ("admin".equals(request.getUsername()) && "admin".equals(request.getPassword())) {
            String token = securityConfig.generateToken(request.getUsername(), List.of("ROLE_ADMIN"));
            return Mono.just(ResponseEntity.ok(LoginResponse.builder()
                    .token(token)
                    .username(request.getUsername())
                    .roles(List.of("ROLE_ADMIN"))
                    .build()));
        } else if ("user".equals(request.getUsername()) && "user".equals(request.getPassword())) {
            String token = securityConfig.generateToken(request.getUsername(), List.of("ROLE_USER"));
            return Mono.just(ResponseEntity.ok(LoginResponse.builder()
                    .token(token)
                    .username(request.getUsername())
                    .roles(List.of("ROLE_USER"))
                    .build()));
        }

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * 验证JWT令牌
     * @param authHeader Authorization请求头
     * @return 令牌是否有效
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token")
    public Mono<ResponseEntity<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = securityConfig.validateToken(token);
            return Mono.just(ResponseEntity.ok(isValid));
        }
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    /**
     * 用户登出接口
     * 生产环境中可实现令牌黑名单机制
     * @return 空响应
     */
    @GetMapping("/logout")
    @Operation(summary = "User logout")
    public Mono<ResponseEntity<Void>> logout() {
        // In a real implementation, you might want to blacklist the token
        return Mono.just(ResponseEntity.ok().<Void>build());
    }
}
