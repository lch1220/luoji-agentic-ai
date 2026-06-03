/*
 * KnowledgeResponse - 知识库查询响应数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 知识库RAG查询响应对象，包含AI生成的答案、来源文档列表和置信度
 */
package com.luoji.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeResponse {
    // AI生成的答案
    private String answer;
    // 会话ID
    private String sessionId;
    // 响应时间戳
    private Instant timestamp;
    // 检索到的来源文档列表
    private List<SourceDocument> sources;
    // 答案置信度
    private double confidence;

    /**
     * 来源文档内部类
     * 包含文档引用信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceDocument {
        private String documentId;  // 文档ID
        private String fileName;    // 文件名
        private String chunk;       // 相关文本块
        private double score;      // 相似度分数
        private int pageNum;       // 页码
    }
}
