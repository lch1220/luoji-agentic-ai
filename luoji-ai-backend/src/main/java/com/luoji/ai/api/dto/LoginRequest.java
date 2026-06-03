/*
 * LoginRequest - 登录请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 用户登录请求对象，包含用户名和密码
 */
package com.luoji.ai.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    // 用户名（必填）
    @NotBlank(message = "Username cannot be blank")
    private String username;

    // 密码（必填）
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
