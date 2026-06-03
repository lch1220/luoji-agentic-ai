/*
 * UploadRequest - 文档上传请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 文档上传请求对象，用于知识库文档上传，包含文件名、内容、类型和分类
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {
    // 文件名
    private String fileName;
    // 文件内容
    private String content;
    // 文件类型（如txt、pdf、doc等）
    private String fileType;
    // 文件分类（用于知识库组织）
    private String category;
}
