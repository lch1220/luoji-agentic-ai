/*
 * UploadResponse - 文档上传响应数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 文档上传响应对象，包含上传结果文档ID、文件名、成功状态和上传时间
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    // 文档ID（上传成功后生成）
    private String documentId;
    // 文件名
    private String fileName;
    // 是否上传成功
    private boolean success;
    // 响应消息（成功或错误信息）
    private String message;
    // 上传时间
    private Instant uploadedAt;
}
