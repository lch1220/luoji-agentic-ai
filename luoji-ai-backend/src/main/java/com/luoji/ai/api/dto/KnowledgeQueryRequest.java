/*
 * KnowledgeQueryRequest - 知识库查询请求数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 知识库RAG查询请求对象，包含查询内容、会话信息和检索参数配置
 */
package com.luoji.ai.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeQueryRequest {
    // 查询内容（必填）
    @NotBlank(message = "Query cannot be blank")
    private String query;
    // 会话ID（可选）
    private String sessionId;
    // 返回的最相似文档数量（可选）
    private int topK;
    // 相似度阈值（可选）
    private double similarityThreshold;
    // 限定查询的分类列表（可选）
    private List<String> categories;
    // 指定使用的模型（可选）
    private String model;
}
