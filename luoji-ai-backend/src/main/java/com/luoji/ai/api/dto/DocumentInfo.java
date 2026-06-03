/*
 * DocumentInfo - 文档信息数据传输对象
 *
 * @author luoji
 * @date 2026-05-28
 * @description 知识库文档信息对象，包含文档的唯一标识、文件信息、上传时间和索引状态
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
public class DocumentInfo {
    private String id;              // 文档唯一标识
    private String fileName;        // 文件名
    private String fileType;        // 文件类型
    private String category;         // 分类
    private long size;              // 文件大小（字节）
    private Instant uploadedAt;      // 上传时间
    private int chunkCount;         // 分块数量
    private String status;          // 状态（indexed、reindexing等）
}
