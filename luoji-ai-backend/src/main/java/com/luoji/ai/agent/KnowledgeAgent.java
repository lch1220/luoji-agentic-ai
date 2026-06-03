/*
 * KnowledgeAgent - 知识库智能体
 *
 * @author luoji
 * @date 2026-05-28
 * @description 知识库智能体，基于RAG（检索增强生成）模式处理知识库问答，
 *              支持文档上传、索引和相似性检索
 */
package com.luoji.ai.agent;

import com.luoji.ai.api.dto.*;
import com.luoji.ai.config.AgentConfig;
import com.luoji.ai.core.llm.LlmClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeAgent {

    private final LlmClient llmClient;
    private final AgentConfig agentConfig;

    // Mock document storage - in production, use MongoDB or similar
    // 模拟文档存储，生产环境应使用MongoDB等数据库
    private final List<DocumentInfo> documentStore = new ArrayList<>();

    /**
     * 上传文档到知识库
     * 生成文档ID，估算分块数，存储文档信息
     * @param request 上传请求
     * @return 上传响应
     */
    public Mono<UploadResponse> uploadDocument(UploadRequest request) {
        log.info("Uploading document: {}", request.getFileName());
        String documentId = UUID.randomUUID().toString();

        DocumentInfo doc = DocumentInfo.builder()
                .id(documentId)
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .category(request.getCategory())
                .uploadedAt(Instant.now())
                .chunkCount(estimateChunkCount(request.getContent()))
                .status("indexed")
                .build();

        documentStore.add(doc);

        return Mono.just(UploadResponse.builder()
                .documentId(documentId)
                .fileName(request.getFileName())
                .success(true)
                .message("Document uploaded successfully")
                .uploadedAt(Instant.now())
                .build());
    }

    /**
     * 获取知识库中所有文档列表
     * @return 文档信息列表
     */
    public Mono<List<DocumentInfo>> listDocuments() {
        return Mono.just(List.copyOf(documentStore));
    }

    /**
     * 删除指定文档
     * @param documentId 文档ID
     * @return 是否删除成功
     */
    public Mono<Boolean> deleteDocument(String documentId) {
        boolean removed = documentStore.removeIf(doc -> doc.getId().equals(documentId));
        return Mono.just(removed);
    }

    /**
     * 处理知识库查询（同步）
     * 检索相关文档，构建RAG提示词，返回答案
     * @param request 查询请求
     * @return 知识库响应
     */
    public Mono<KnowledgeResponse> query(KnowledgeQueryRequest request) {
        log.info("Processing knowledge query: {}", request.getQuery());

        int topK = request.getTopK() > 0 ? request.getTopK() : agentConfig.getKnowledge().getTopK();
        double threshold = request.getSimilarityThreshold() > 0
                ? request.getSimilarityThreshold()
                : agentConfig.getKnowledge().getSimilarityThreshold();

        // Mock retrieval - in production, use vector similarity search
        // 模拟检索，生产环境应使用Milvus/Pinecone等向量数据库进行相似性搜索
        List<KnowledgeResponse.SourceDocument> sources = retrieveRelevantChunks(request.getQuery(), topK, threshold);

        // 构建上下文字符串
        String context = sources.stream()
                .map(s -> "[" + s.getFileName() + "] " + s.getChunk())
                .reduce((a, b) -> a + "\n\n" + b)
                .orElse("No relevant documents found.");

        // RAG提示词模板
        String prompt = """
                Based on the following context from the knowledge base, answer the query.

                Context:
                %s

                Query: %s

                Please provide a detailed and accurate answer based on the context above.
                If the context doesn't contain relevant information, say so.
                """.formatted(context, request.getQuery());

        return llmClient.generate(prompt)
                .map(answer -> KnowledgeResponse.builder()
                        .answer(answer)
                        .sessionId(request.getSessionId())
                        .timestamp(Instant.now())
                        .sources(sources)
                        .confidence(calculateConfidence(sources))
                        .build());
    }

    /**
     * 处理知识库流式查询
     * @param request 查询请求
     * @return 响应流
     */
    public Flux<KnowledgeResponse> queryStream(KnowledgeQueryRequest request) {
        log.info("Processing streaming knowledge query: {}", request.getQuery());

        List<KnowledgeResponse.SourceDocument> sources = retrieveRelevantChunks(
                request.getQuery(),
                agentConfig.getKnowledge().getTopK(),
                agentConfig.getKnowledge().getSimilarityThreshold()
        );

        String context = sources.stream()
                .map(s -> "[" + s.getFileName() + "] " + s.getChunk())
                .reduce((a, b) -> a + "\n\n" + b)
                .orElse("No relevant documents found.");

        String prompt = """
                Based on the following context from the knowledge base, answer the query.

                Context:
                %s

                Query: %s

                Please provide a detailed and accurate answer based on the context above.
                """.formatted(context, request.getQuery());

        return llmClient.generateStream(prompt)
                .map(chunk -> KnowledgeResponse.builder()
                        .answer(chunk)
                        .sessionId(request.getSessionId())
                        .timestamp(Instant.now())
                        .sources(sources)
                        .build());
    }

    /**
     * 重建知识库索引
     * 将所有文档状态设置为reindexing再设置为indexed
     * @return 是否成功
     */
    public Mono<Boolean> rebuildIndex() {
        log.info("Rebuilding knowledge base index");
        documentStore.forEach(doc -> doc.setStatus("reindexing"));
        documentStore.forEach(doc -> doc.setStatus("indexed"));
        return Mono.just(true);
    }

    /**
     * 检索相关文档块
     * 模拟向量相似性搜索，返回topK个最相关的文档
     * @param query 查询内容
     * @param topK 返回数量
     * @param threshold 相似度阈值
     * @return 相关文档列表
     */
    private List<KnowledgeResponse.SourceDocument> retrieveRelevantChunks(String query, int topK, double threshold) {
        // Mock implementation - in production, use vector similarity search with Milvus/Pinecone/etc.
        if (documentStore.isEmpty()) {
            return List.of();
        }
        return documentStore.stream()
                .limit(topK)
                .map(doc -> KnowledgeResponse.SourceDocument.builder()
                        .documentId(doc.getId())
                        .fileName(doc.getFileName())
                        .chunk("Relevant chunk from " + doc.getFileName())
                        .score(0.95)
                        .pageNum(1)
                        .build())
                .toList();
    }

    /**
     * 估算文档的分块数量
     * 按每500字符一个分块估算
     * @param content 文档内容
     * @return 分块数量
     */
    private int estimateChunkCount(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil((double) content.length() / 500);
    }

    /**
     * 计算答案置信度
     * 基于来源文档的相似度分数计算平均值
     * @param sources 来源文档列表
     * @return 置信度分数
     */
    private double calculateConfidence(List<KnowledgeResponse.SourceDocument> sources) {
        if (sources == null || sources.isEmpty()) {
            return 0.0;
        }
        return sources.stream()
                .mapToDouble(KnowledgeResponse.SourceDocument::getScore)
                .average()
                .orElse(0.0);
    }
}
