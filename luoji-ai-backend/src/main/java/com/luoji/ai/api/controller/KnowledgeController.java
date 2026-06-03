/*
 * KnowledgeController - 知识库控制器
 *
 * @author luoji
 * @date 2026-05-28
 * @description 知识库管理API控制器，提供文档上传、删除、列表查询、RAG检索等功能，
 *              支持同步和流式查询，返回来源文档信息
 */
package com.luoji.ai.api.controller;

import com.luoji.ai.agent.KnowledgeAgent;
import com.luoji.ai.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/knowledge")
@RequiredArgsConstructor
@Tag(name = "Knowledge", description = "Knowledge base management and RAG Q&A")
public class KnowledgeController {

    private final KnowledgeAgent knowledgeAgent;

    /**
     * 上传文档到知识库
     * @param request 上传请求（包含文件内容）
     * @return 上传响应
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload document to knowledge base")
    public Mono<UploadResponse> uploadDocument(@RequestBody UploadRequest request) {
        log.info("Uploading document: {}", request.getFileName());
        return knowledgeAgent.uploadDocument(request);
    }

    /**
     * 通过multipart/form-data上传文件
     * @param file 上传的文件
     * @param category 文件分类（可选）
     * @return 上传响应
     */
    @PostMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file to knowledge base")
    public Mono<UploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false, defaultValue = "default") String category) {
        log.info("Uploading file: {}, size: {}", file.getOriginalFilename(), file.getSize());
        try {
            String content = new String(file.getBytes());
            String fileName = file.getOriginalFilename();
            // 从文件名提取文件类型
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            UploadRequest request = UploadRequest.builder()
                    .fileName(fileName)
                    .fileType(fileType)
                    .category(category)
                    .content(content)
                    .build();
            return knowledgeAgent.uploadDocument(request);
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            return Mono.just(UploadResponse.builder()
                    .success(false)
                    .message("Failed to upload file: " + e.getMessage())
                    .build());
        }
    }

    /**
     * 获取知识库中所有文档列表
     * @return 文档信息列表
     */
    @GetMapping("/documents")
    @Operation(summary = "List all documents in knowledge base")
    public Mono<List<DocumentInfo>> listDocuments() {
        return knowledgeAgent.listDocuments();
    }

    /**
     * 从知识库删除指定文档
     * @param id 文档ID
     * @return 是否删除成功
     */
    @DeleteMapping("/documents/{id}")
    @Operation(summary = "Delete a document from knowledge base")
    public Mono<Boolean> deleteDocument(@PathVariable String id) {
        log.info("Deleting document: {}", id);
        return knowledgeAgent.deleteDocument(id);
    }

    /**
     * 查询知识库（同步）
     * 使用RAG（检索增强生成）基于知识库内容回答问题
     * @param request 查询请求
     * @return 知识库响应
     */
    @PostMapping("/query")
    @Operation(summary = "Query knowledge base")
    public Mono<KnowledgeResponse> query(@RequestBody KnowledgeQueryRequest request) {
        log.info("Knowledge query: {}", request.getQuery());
        return knowledgeAgent.query(request);
    }

    /**
     * 流式查询知识库
     * 通过SSE返回流式响应
     * @param request 查询请求
     * @return SSE流
     */
    @PostMapping(value = "/query/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Query knowledge base with streaming response")
    public Flux<ServerSentEvent<KnowledgeResponse>> queryStream(@RequestBody KnowledgeQueryRequest request) {
        log.info("Streaming knowledge query: {}", request.getQuery());
        return knowledgeAgent.queryStream(request)
                .map(response -> ServerSentEvent.<KnowledgeResponse>builder()
                        .data(response)
                        .build());
    }

    /**
     * 重建知识库索引
     * 用于文档更新后重新索引
     * @return 是否成功
     */
    @PostMapping("/index")
    @Operation(summary = "Rebuild knowledge base index")
    public Mono<Boolean> rebuildIndex() {
        log.info("Rebuilding knowledge base index");
        return knowledgeAgent.rebuildIndex();
    }
}
