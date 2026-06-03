/*
 * LoginResponse - 登录响应数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 用户登录成功响应对象，包含JWT令牌、用户名和用户角色列表
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    // JWT认证令牌
    private String token;
    // 用户名
    private String username;
    // 用户角色列表
    private List<String> roles;
}
