# AI智能体框架需求文档

## 1. 项目概述

### 1.1 项目名称
**luoji-ai** - 逻辑AI智能体框架

### 1.2 项目定位
一个通用的、可独立部署的Java AI智能体框架，通过SKILL/MCP协议与业务系统交互，不侵入业务系统，作为企业中AI能力的中台服务存在。

### 1.3 核心价值
- **统一入口**：为企业各业务系统提供统一的AI智能体服务
- **配置驱动**：通过配置文件即可接入不同大模型、向量数据库和业务系统
- **高可用**：具备熔断、降级、重试等容错机制
- **可扩展**：模块化设计，支持SKILL和MCP协议扩展

---

## 2. 功能模块设计

### 2.1 核心功能矩阵

| 功能模块 | 描述 | 优先级 |
|---------|------|--------|
| **快速问答** | 实时问答交互，支持流式输出、多模态 | P0 |
| **知识库问答** | 基于向量数据库的RAG问答，支持重排序/混合检索 | P0 |
| **智能分析规划** | 复杂任务分解与执行规划 | P0 |
| **推理引擎** | 支持CoT/ToT等推理策略 | P0 |
| **工具调用** | Function Calling / Tool Use | P0 |
| **MCP集成** | 支持MCP协议的MCP Client（含生命周期管理） | P0 |
| **SKILL系统** | 可扩展的技能/插件系统 | P1 |
| **钩子系统** | Agent各阶段的拦截与处理 | P1 |
| **记忆系统** | 会话记忆、上下文管理、会话生命周期 | P1 |
| **多模型路由** | 支持多模型动态路由 | P2 |
| **安全合规** | 认证授权、内容审核、PII脱敏、审计日志 | P2 |
| **多模态支持** | 图片/音频/文档输入，文本/Markdown/HTML输出 | P2 |
| **可观测性** | 监控指标、链路追踪、日志聚合、告警 | P1 |
| **上下文管理** | Token预算控制、上下文压缩、窗口耗尽处理 | P1 |
| **成本控制** | 用量统计、配额管理、成本预警 | P2 |
| **异步任务** | 长时间任务队列、状态追踪、Webhook回调 | P2 |
| **响应缓存** | LLM响应缓存、Embedding缓存 | P2 |

---

## 3. 技术架构

### 3.1 技术栈选型

| 层级 | 技术选型 | 说明 |
|-----|---------|------|
| **基础框架** | Spring Boot 3.2+ | 业界通用，生态成熟 |
| **异步响应式** | Spring WebFlux + Project Reactor | 非阻塞IO，WebFlux异步驱动 |
| **AI集成层** | Spring AI Alibaba / LangChain4j | 支持国内主流大模型 |
| **向量数据库** | Milvus + Qdrant | 同时支持，通过配置切换 |
| **通信协议** | REST + SSE + WebSocket + RSocket | 全面支持实时通信 |
| **传统数据库** | MySQL 8+ | 元数据、配置存储 |
| **缓存层** | Redis + Redisson | 会话缓存、限流、分布式锁 |
| **容错治理** | Resilience4j | 熔断、降级、重试、超时 |
| **协议集成** | MCP Java SDK / SKILL SDK | 与业务系统交互 |
| **配置中心** | Spring Cloud Config / Nacos | 配置热更新、多环境 |
| **构建工具** | Maven | Java 21 |
| **API文档** | SpringDoc OpenAPI 3 | Swagger文档 |

### 3.2 核心架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway Layer                         │
│                  (REST API / SSE / WebSocket)                    │
├─────────────────────────────────────────────────────────────────┤
│                         Agent Core Layer                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │ FastQ&A  │ │Knowledge │ │Analysis  │ │Reasoning │            │
│  │ Agent    │ │  Agent   │ │ Planner  │ │  Engine  │            │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │
├─────────────────────────────────────────────────────────────────┤
│                        Tool System Layer                         │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │   MCP    │ │  SKILL   │ │Function  │ │  Hook    │            │
│  │  Client  │ │ Registry │ │ Calling  │ │ System   │            │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │
├─────────────────────────────────────────────────────────────────┤
│                       Memory & Context Layer                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                         │
│  │ Session  │ │ Vector   │ │  Tool    │                         │
│  │ Memory   │ │  Store   │ │ Results  │                         │
│  └──────────┘ └──────────┘ └──────────┘                         │
├─────────────────────────────────────────────────────────────────┤
│                    LLM & External Integration Layer              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │Spring AI │ │ LangChain│ │  Model   │ │ VectorDB │            │
│  │Alibaba   │ │   4j     │ │ Router   │ │  Client  │            │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │
├─────────────────────────────────────────────────────────────────┤
│                     Resilience & Governance Layer                │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │ Circuit  │ │  Rate    │ │ Retry    │ │ Timeout  │            │
│  │ Breaker  │ │  Limiter │ │ Policy   │ │ Manager  │            │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. 详细功能规格

### 4.1 快速问答模块 (FastQ&A)

**功能描述**：基础的实时问答能力，支持流式输出和多模态交互。

**核心能力**：
- 支持多种流式响应协议（SSE、WebSocket、RSocket）
- 流式响应支持中断（客户端发送终止信号）
- 支持部分内容缓存与断点续传（长文本生成场景）
- 多轮对话上下文管理
- 多模态输入：图片（OCR/描述）、音频（ASR）、PDF（图文混排）
- 多模态输出：文本、Markdown表格、HTML片段、图片URL（生图工具调用）
- 模型层适配：GPT-4V、Qwen-VL、Claude-3等
- 敏感词过滤
- 响应内容审核

**配置项**：
```yaml
agent:
  fastqa:
    default-model: gpt-4
    max-tokens: 4096
    temperature: 0.7
    stream-enabled: true
```

### 4.2 知识库问答模块 (Knowledge Q&A)

**功能描述**：基于RAG模式的向量知识库问答，支持高级检索策略。

**核心能力**：
- 多向量数据库支持（Milvus/Qdrant）
- 混合检索（向量相似度 + BM25关键词，可配置权重）
- 检索后重排序（Rerank，使用Cross-Encoder如BGE-reranker）
- 查询改写（多轮对话指代消解、省略补全）
- 知识库CRUD管理API
- 文档解析（PDF、Word、TXT、Markdown，支持图文混排）
- 分块策略配置（固定Token/按标点符号/按文档标题/语义分割）
- 元数据过滤（文档来源、页码、更新时间）
- 知识库增量更新（版本管理、变更检测、增量embedding）
- 权限隔离（基于Milvus Partition Key的租户/用户控制）
- 引用追溯

**配置项**：
```yaml
agent:
  knowledge:
    default-model: gpt-4
    vector-db:
      type: milvus  # milvus | qdrant
      host: localhost
      port: 19530
      collection: knowledge_base
    retrieval:
      top-k: 5
      similarity-threshold: 0.75
      rerank:
        enabled: true
        model: BAAI/bge-reranker-v2-m3
        top-n: 3
      hybrid-search:
        vector-weight: 0.7
        keyword-weight: 0.3
      query-rewrite:
        enabled: true
        model: gpt-3.5-turbo
    chunking:
      strategy: semantic  # fixed | punctuation | title | semantic
      chunk-size: 512
      overlap: 50
```

### 4.3 智能分析规划模块 (Analysis & Planning)

**功能描述**：复杂任务的分解与执行规划。

**核心能力**：
- 任务自动分解（LLM驱动）
- 执行计划生成
- 计划执行与监控
- 计划调整与回溯
- 并行任务调度

**配置项**：
```yaml
agent:
  planner:
    default-model: gpt-4
    max-steps: 10
    enable-parallel: true
    plan-refinement: true
```

### 4.4 推理引擎模块 (Reasoning Engine)

**功能描述**：支持多种推理策略的推理引擎。

**核心能力**：
- Chain-of-Thought (CoT)
- Tree-of-Thought (ToT)
- ReAct (Reasoning + Acting)
- 自定义推理策略扩展

**配置项**：
```yaml
agent:
  reasoning:
    default-model: gpt-4
    strategy: react  # cot | tot | react
    max-thought-steps: 15
    enable-reflection: true
```

### 4.5 工具调用系统 (Tool Calling)

**功能描述**：统一的工具调用框架。

**核心能力**：
- Function Calling标准实现
- 工具注册与发现机制
- 工具执行与结果处理
- 工具调用日志与审计

### 4.6 MCP集成模块

**功能描述**：支持MCP协议的客户端，与业务系统交互。

**核心能力**：
- MCP 1.0 Client实现（兼容0.9.0，协议版本协商降级）
- 多MCP Server管理（健康检查、断线重连、自动注册发现）
- 资源访问（Resource）
- 工具调用（Tool）
- 提示模板（Prompt）
- MCP Server生命周期管理（基于Consul/Nacos服务发现）
- MCP资源访问控制（基于OAuth2 Scope权限校验）

**配置项**：
```yaml
agent:
  mcp:
    servers:
      - name: business-system-a
        type: sse  # sse | http
        url: http://localhost:8080/mcp
        auth:
          type: bearer
          token: ${MCP_AUTH_TOKEN}
      - name: business-system-b
        type: http
        url: http://localhost:8081/mcp
    lifecycle:
      health-check-interval: 30s
      reconnect-enabled: true
      discovery:
        type: consul  # consul | nacul | static
        url: http://localhost:8500
```

### 4.7 SKILL系统

**功能描述**：可扩展的技能/插件系统。

**核心能力**：
- SKILL定义标准（JSON Schema）
- 动态SKILL加载
- SKILL编排与组合
- 内置常用SKILL：
  - `calculator` - 数学计算
  - `datetime` - 日期时间
  - `web_search` - 网页搜索
  - `code_executor` - 代码执行

**配置项**：
```yaml
agent:
  skills:
    enabled: true
    registry:
      - name: calculator
        type: built-in
      - name: web-search
        type: built-in
        config:
          api-key: ${SEARCH_API_KEY}
```

### 4.8 钩子系统 (Hook System)

**功能描述**：在Agent关键节点插入自定义逻辑。

**核心能力**：
- 请求前钩子（Pre-Request）
- 响应后钩子（Post-Response）
- 错误处理钩子（On-Error）
- 工具执行钩子（Pre/Post-Tool）
- 会话事件钩子（Session Events）

**钩子类型**：
| 钩子类型 | 触发时机 | 用途 |
|---------|---------|------|
| `pre-request` | LLM调用前 | 参数校验、日志、限流 |
| `post-response` | LLM调用后 | 结果处理、日志、敏感词过滤 |
| `on-error` | 异常发生时 | 降级处理、告警 |
| `pre-tool` | 工具调用前 | 参数预处理 |
| `post-tool` | 工具调用后 | 结果转换、日志 |

### 4.9 记忆系统 (Memory System)

**功能描述**：会话记忆与上下文管理，包含完整的会话生命周期。

**会话状态机**：
```
INIT -> ACTIVE -> IDLE -> CLOSED/EXPIRED
```

**核心能力**：
- 短期记忆（会话内）
- 长期记忆（持久化）
- 记忆摘要压缩
- 记忆检索
- 会话超时管理（空闲超时、绝对超时）
- 最大消息轮数控制
- 会话状态持久化（内存/Redis/数据库三种模式）
- 会话锁机制（基于Redisson，防止并发请求导致上下文错乱）

**配置项**：
```yaml
agent:
  session:
    timeout:
      idle-seconds: 1800      # 空闲超时（默认30分钟）
      absolute-seconds: 86400  # 绝对超时（默认24小时）
    max-turns: 200            # 最大消息轮数
    store-type: redis         # memory | redis | db
    lock-enabled: true        # 启用会话锁
  memory:
    short-term:
      max-messages: 20
      summary-trigger: 15
    long-term:
      enabled: true
      embedding-model: text-embedding-ada-002
      max-memory-items: 100
```

### 4.10 可观测性模块 (Observability)

**功能描述**：完整的可观测性体系，支持监控指标、链路追踪、日志聚合和告警。

**核心能力**：
- **指标采集**：Token用量、响应延迟、错误率、QPS等
- **分布式追踪**：OpenTelemetry/Jaeger请求链路追踪
- **日志聚合**：结构化日志 + ELK/Loki集成
- **告警机制**：基于Prometheus AlertManager的阈值告警
- **健康检查**：多维度探针（LLM/向量库/Redis/MCP）

**配置项**：
```yaml
agent:
  observability:
    metrics:
      enabled: true
      export-interval: 30s
    tracing:
      enabled: true
      sampler: trace-id-ratio  # always | trace-id-ratio
      sample-ratio: 0.1
    logging:
      format: json
      level: INFO
    alerting:
      enabled: true
      rules:
        - name: high-error-rate
          condition: error-rate > 0.05
          severity: critical
        - name: high-latency
          condition: p99-latency > 10s
          severity: warning
```

### 4.11 上下文管理模块 (Context Management)

**功能描述**：Token预算控制和上下文窗口管理。

**核心能力**：
- **Token预算控制**：单次请求Token上限、会话累计Token上限
- **上下文压缩**：窗口耗尽前自动摘要（基于LLM的压缩）
- **上下文耗尽处理**：主动提示用户或触发摘要
- **Token计费**：精确计算输入/输出Token用量

**配置项**：
```yaml
agent:
  context:
    budget:
      single-request-limit: 128000  # 单次请求Token上限
      session-limit: 512000         # 会话累计Token上限
    compression:
      enabled: true
      trigger-ratio: 0.8           # 达到80%时触发压缩
      compression-model: gpt-3.5-turbo
    exhaustion:
      strategy: summarize  # summarize | truncate | reject
```

### 4.12 成本控制模块 (Cost Control)

**功能描述**：用量统计、配额管理和成本预警。

**核心能力**：
- **用量统计**：按租户/用户/模型统计Token用量和费用
- **配额管理**：防止异常耗尽，支持配额上限设置
- **成本预警**：月度/日度阈值告警
- **成本报表**：支持导出CSV/Excel报表

**配置项**：
```yaml
agent:
  cost:
    quota:
      enabled: true
      default-monthly-limit: 10000  # 默认月度配额（美元）
    alert:
      enabled: true
      thresholds:
        daily: 500   # 日度阈值（美元）
        monthly: 8000 # 月度阈值（美元）
    pricing:
      gpt-4: 0.03      # 输入Token价格（美元/1K）
      gpt-4-output: 0.06 # 输出Token价格
      gpt-3.5: 0.002
```

### 4.13 异步任务模块 (Async Task)

**功能描述**：长时间运行任务队列和状态追踪。

**核心能力**：
- **任务队列**：长时间任务统一入队管理
- **状态追踪**：PENDING/RUNNING/SUCCESS/FAILED/CANCELLED
- **进度查询**：实时获取任务执行进度
- **Webhook回调**：任务完成后主动通知
- **任务取消**：支持主动取消运行中任务

**API扩展**：
| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | `/api/v1/tasks` | 创建异步任务 |
| GET | `/api/v1/tasks/{id}` | 查询任务状态 |
| POST | `/api/v1/tasks/{id}/cancel` | 取消任务 |
| GET | `/api/v1/tasks/{id}/progress` | 获取任务进度 |

### 4.14 响应缓存模块 (Response Cache)

**功能描述**：LLM响应缓存和Embedding缓存。

**核心能力**：
- **语义缓存**：基于向量相似度的Query缓存（Similarity > 0.95）
- **Embedding缓存**：高频Query的Embedding结果缓存
- **缓存策略**：LRU淘汰、最大条目限制
- **缓存穿透防护**：基于null结果的标记

**配置项**：
```yaml
agent:
  cache:
    response:
      enabled: true
      similarity-threshold: 0.95
      max-entries: 10000
      ttl: 24h
    embedding:
      enabled: true
      max-entries: 50000
      ttl: 7d
```

---

## 5. 容错与治理机制

### 5.1 熔断器 (Circuit Breaker)

| 配置项 | 默认值 | 说明 |
|-------|-------|------|
| failure-rate-threshold | 50% | 熔断阈值 |
| slow-call-rate-threshold | 80% | 慢调用熔断率 |
| slow-call-duration-threshold | 2s | 慢调用阈值 |
| wait-duration-in-open-state | 60s | 熔断持续时间 |
| permitted-calls-in-half-open | 3 | 半开状态允许调用数 |

### 5.2 重试机制 (Retry)

| 配置项 | 默认值 | 说明 |
|-------|-------|------|
| max-attempts | 3 | 最大重试次数 |
| wait-duration | 1s | 重试间隔 |
| retryable-exceptions | Exception | 可重试异常 |
| non-retryable-exceptions | IllegalArgumentException | 不可重试异常 |

### 5.3 限流 (Rate Limiting)

| 配置项 | 默认值 | 说明 |
|-------|-------|------|
| requests-permitted | 100 | 每周期允许请求数 |
| limit-refresh-period | 1m | 限流周期 |
| timeout-duration | 500ms | 等待超时 |

### 5.4 超时控制 (Timeout)

| 配置项 | 默认值 | 说明 |
|-------|-------|------|
| LLM-call-timeout | 30s | LLM调用超时 |
| tool-call-timeout | 10s | 工具调用超时 |
| total-request-timeout | 60s | 整个请求超时 |

### 5.5 降级策略

**降级层级**：
1. **一级降级**：流式转同步
2. **二级降级**：切换模型（GPT-4 → GPT-3.5）
3. **三级降级**：返回预设回复
4. **四级降级**：返回友好错误

---

## 5.1 异步非阻塞架构

**设计原则**：在LLM调用、向量检索等IO密集型操作上使用WebFlux + Project Reactor，避免传统Servlet线程阻塞。

**核心实现**：
```java
public Mono<ChatResponse> streamChat(ChatRequest request) {
    return memoryManager.loadHistory(request.getSessionId())
        .flatMap(history -> llmClient.stream(request, history))
        .doOnNext(chunk -> saveToCache(chunk));
}
```

**技术优势**：
- 更高的并发处理能力（单线程承载更多请求）
- 背压控制（RSocket协议原生支持）
- 流式响应更高效（WebFlux + Flux）

---

## 5.2 配置热更新与多环境

**支持方式**：
- Spring Cloud Config 配置中心
- Nacos 配置中心（推荐，支持更丰富的配置管理）

**热更新范围**：
- 模型路由权重
- 限流阈值
- 重试策略参数
- 部分Agent参数

**配置示例**：
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/luoji-ai-config
  config:
    profile: ${ENV:dev}
```

---

## 5.3 安全合规

### 5.3.1 认证与授权

| 层级 | 实现方式 |
|-----|---------|
| API认证 | JWT（内置）或对接OAuth2/OIDC（Keycloak） |
| 租户隔离 | API Key + Tenant ID，请求头X-Tenant-Id |
| 模型调用授权 | 按租户/用户限制可使用的模型、工具、知识库 |
| 审计日志 | 全量记录：用户ID、会话ID、输入输出、调用的工具、耗时、Token用量 |

### 5.3.2 内容安全增强

| 功能 | 说明 |
|-----|------|
| 输入过滤 | Prompt注入检测（正则 + 模型分类器） |
| 输出审核 | 阿里云绿网、腾讯天御等第三方API集成 |
| PII脱敏 | 自动识别并脱敏手机号、身份证、银行卡（基于Spy库） |

### 5.3.3 敏感信息存储

- API Key、Token等使用AWS KMS或HashiCorp Vault加密存储
- 内存中的敏感数据（如用户对话中的密码）使用char[]并定期清零

---

## 6. API设计

### 6.1 核心API端点

| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | `/api/v1/chat` | 快速问答 |
| POST | `/api/v1/chat/stream` | 流式问答(SSE) |
| WS | `/api/v1/chat/ws` | WebSocket实时问答（STOMP子协议） |
| RS | `/api/v1/chat/rsocket` | RSocket高性能流式（可选扩展） |
| POST | `/api/v1/knowledge/query` | 知识库问答 |
| POST | `/api/v1/knowledge/documents` | 知识库文档管理 |
| POST | `/api/v1/agent/execute` | 执行智能体任务 |
| POST | `/api/v1/reason` | 推理请求 |
| GET | `/api/v1/skills` | 获取可用技能列表 |
| POST | `/api/v1/skills/execute` | 执行技能 |
| GET | `/api/v1/health` | 健康检查 |
| POST | `/api/v1/tasks` | 创建异步任务 |
| GET | `/api/v1/tasks/{id}` | 查询任务状态 |
| POST | `/api/v1/tasks/{id}/cancel` | 取消任务 |
| GET | `/api/v1/tasks/{id}/progress` | 获取任务进度 |
| GET | `/api/v1/metrics` | 获取监控指标 |
| GET | `/api/v1/cost/usage` | 获取用量统计 |

### 6.2 流式响应协议对比

| 协议 | 适用场景 | 实现方式 |
|-----|---------|---------|
| SSE | 单向流式文本生成 | Spring WebFlux + Flux |
| WebSocket | 双向交互（如Agent中途询问用户） | STOMP子协议 |
| RSocket | 高性能流式、背压控制 | 可选扩展 |

**流式特性**：
- 支持中断（客户端发送终止信号）
- 支持部分内容缓存与断点续传（长文本生成场景）

### 6.2 请求/响应标准格式

**统一请求**：
```json
{
  "sessionId": "uuid",
  "model": "gpt-4",
  "messages": [
    {"role": "user", "content": "你好"}
  ],
  "parameters": {
    "temperature": 0.7,
    "maxTokens": 4096
  },
  "metadata": {
    "userId": "user-001",
    "traceId": "trace-xxx"
  }
}
```

**统一响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "回复内容",
    "usage": {
      "promptTokens": 100,
      "completionTokens": 50
    },
    "traceId": "trace-xxx"
  }
}
```

---

## 7. 配置管理

### 7.1 配置文件结构

```yaml
# application.yml

server:
  port: 8080

spring:
  application:
    name: luoji-ai

# LLM模型配置
llm:
  providers:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: https://api.openai.com/v1
      models:
        default: gpt-4
        gpt-4:
          name: gpt-4
          max-tokens: 4096
        gpt-3.5:
          name: gpt-3.5-turbo
          max-tokens: 4096
    alibaba:
      api-key: ${ALIBABA_API_KEY}
      base-url: https://dashscope.aliyuncs.com/api/v1
      models:
        default: qwen-turbo
        qwen-plus:
          name: qwen-plus
          max-tokens: 8192

# Agent核心配置
agent:
  default-provider: openai
  fastqa:
    enabled: true
    default-model: gpt-4
  knowledge:
    enabled: true
    vector-db:
      type: milvus
      host: ${MILVUS_HOST:localhost}
      port: ${MILVUS_PORT:19530}
  planner:
    enabled: true
  reasoning:
    enabled: true
    strategy: react

# MCP配置
mcp:
  enabled: true
  servers: []

# SKILL配置
skill:
  enabled: true
  registry: []

# 记忆配置
memory:
  enabled: true
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}

# 会话配置
agent:
  session:
    timeout:
      idle-seconds: 1800
      absolute-seconds: 86400
    max-turns: 200
    store-type: redis
    lock-enabled: true

# Resilience配置
resilience:
  circuit-breaker:
    failure-rate-threshold: 50
    wait-duration-in-open-state: 60s
  retry:
    max-attempts: 3
    wait-duration: 1s
  timeout:
    llm-call-timeout: 30s
```

---

## 8. 目录结构设计

```
luoji-ai/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/luoji/ai/
│   │   │       ├── LuoJiAiApplication.java
│   │   │       ├── config/
│   │   │       │   ├── LlmConfig.java
│   │   │       │   ├── AgentConfig.java
│   │   │       │   ├── McpConfig.java
│   │   │       │   ├── SkillConfig.java
│   │   │       │   ├── MemoryConfig.java
│   │   │       │   ├── ResilienceConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── SessionConfig.java
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   ├── ContentFilter.java
│   │   │       │   └── PiiMasker.java
│   │   │       ├── agent/
│   │   │       │   ├── Agent.java
│   │   │       │   ├── FastQAAgent.java
│   │   │       │   ├── KnowledgeAgent.java
│   │   │       │   ├── PlanningAgent.java
│   │   │       │   └── ReasoningEngine.java
│   │   │       ├── core/
│   │   │       │   ├── llm/
│   │   │       │   │   ├── LlmClient.java
│   │   │       │   │   ├── LlmProvider.java
│   │   │       │   │   ├── ModelRouter.java
│   │   │       │   │   └── LlmResponse.java
│   │   │       │   ├── memory/
│   │   │       │   │   ├── MemoryManager.java
│   │   │       │   │   ├── SessionManager.java
│   │   │       │   │   ├── ShortTermMemory.java
│   │   │       │   │   └── LongTermMemory.java
│   │   │       │   ├── tools/
│   │   │       │   │   ├── Tool.java
│   │   │       │   │   ├── ToolRegistry.java
│   │   │       │   │   └── ToolResult.java
│   │   │       │   └── hook/
│   │   │       │       ├── Hook.java
│   │   │       │       ├── HookRegistry.java
│   │   │       │       └── HookContext.java
│   │   │       ├── mcp/
│   │   │       │   ├── McpClient.java
│   │   │       │   ├── McpServerConfig.java
│   │   │       │   ├── McpResource.java
│   │   │       │   ├── McpTool.java
│   │   │       │   └── McpPrompt.java
│   │   │       ├── skill/
│   │   │       │   ├── Skill.java
│   │   │       │   ├── SkillRegistry.java
│   │   │       │   ├── SkillExecutor.java
│   │   │       │   └── builtins/
│   │   │       │       ├── CalculatorSkill.java
│   │   │       │       ├── DateTimeSkill.java
│   │   │       │       └── WebSearchSkill.java
│   │   │       ├── vectorstore/
│   │   │       │   ├── VectorStore.java
│   │   │       │   ├── MilvusVectorStore.java
│   │   │       │   ├── QdrantVectorStore.java
│   │   │       │   └── Document.java
│   │   │       ├── rag/
│   │   │       │   ├── Reranker.java
│   │   │       │   ├── QueryRewriter.java
│   │   │       │   ├── HybridSearch.java
│   │   │       │   └── ChunkingStrategy.java
│   │   │       ├── api/
│   │   │       │   ├── controller/
│   │   │       │   │   ├── ChatController.java
│   │   │       │   │   ├── KnowledgeController.java
│   │   │       │   │   ├── AgentController.java
│   │   │       │   │   ├── SkillController.java
│   │   │       │   │   └── WebSocketController.java
│   │   │       │   ├── websocket/
│   │   │       │   │   └── ChatWebSocketHandler.java
│   │   │       │   ├── request/
│   │   │       │   │   ├── ChatRequest.java
│   │   │       │   │   ├── KnowledgeQueryRequest.java
│   │   │       │   │   └── AgentExecuteRequest.java
│   │   │       │   └── response/
│   │   │       │       └── ApiResponse.java
│   │   │       ├── resilience/
│   │   │       │   ├── CircuitBreakerManager.java
│   │   │       │   ├── RetryManager.java
│   │   │       │   ├── RateLimiter.java
│   │   │       │   └── TimeoutManager.java
│   │   │       ├── observability/
│   │   │       │   ├── MetricsCollector.java
│   │   │       │   ├── TracingManager.java
│   │   │       │   ├── AlertManager.java
│   │   │       │   └── HealthChecker.java
│   │   │       ├── context/
│   │   │       │   ├── ContextManager.java
│   │   │       │   ├── TokenBudgetManager.java
│   │   │       │   └── ContextCompressor.java
│   │   │       ├── cost/
│   │   │       │   ├── CostTracker.java
│   │   │       │   ├── QuotaManager.java
│   │   │       │   └── CostAlertService.java
│   │   │       ├── task/
│   │   │       │   ├── TaskQueue.java
│   │   │       │   ├── TaskStatus.java
│   │   │       │   └── TaskCallback.java
│   │   │       └── cache/
│   │   │           ├── ResponseCache.java
│   │   │           └── EmbeddingCache.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── config/
│   │           └── agent-config.yaml
│   └── test/
│       └── java/
│           └── com/luoji/ai/
│               ├── agent/
│               ├── api/
│               └── resilience/
└── README.md
```

---

## 9. 依赖说明

### 9.1 核心依赖

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- AI集成 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-starter</artifactId>
    </dependency>

    <!-- 向量数据库 -->
    <dependency>
        <groupId>io.milvus</groupId>
        <artifactId>milvus-sdk-java</artifactId>
    </dependency>

    <!-- MCP -->
    <dependency>
        <groupId>io.github.modelcontextprotocol</groupId>
        <artifactId>mcp-java-sdk</artifactId>
    </dependency>

    <!-- 容错 -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-circuitbreaker</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-retry</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-timelimiter</artifactId>
    </dependency>

    <!-- 工具 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    </dependency>

    <!-- 分布式锁与缓存 -->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-spring-boot-starter</artifactId>
    </dependency>

    <!-- 配置中心 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>

    <!-- 安全 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>

    <!-- RSocket（可选） -->
    <dependency>
        <groupId>io.rsocket</groupId>
        <artifactId>rsocket-core</artifactId>
    </dependency>

    <!-- 可观测性 -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId>
    </dependency>
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-spring-boot-starter</artifactId>
    </dependency>

    <!-- 异步任务 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- Caffeine本地缓存 -->
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
    </dependency>
</dependencies>
```

---

## 10. 部署架构

### 10.1 部署模式

```
                    ┌─────────────────┐
                    │   Nginx/LB      │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
        ┌─────▼─────┐  ┌─────▼─────┐  ┌─────▼─────┐
        │  luoji-ai │  │  luoji-ai │  │  luoji-ai │
        │  Instance │  │  Instance │  │  Instance │
        └─────┬─────┘  └─────┬─────┘  └─────┬─────┘
              │              │              │
              └──────────────┼──────────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
   ┌─────▼─────┐       ┌─────▼─────┐      ┌─────▼─────┐
   │   Redis   │       │  Milvus/  │      │  MySQL    │
   │  Cluster  │       │  Qdrant   │      │           │
   └───────────┘       └───────────┘      └───────────┘
```

### 10.2 环境要求

- JDK 21+
- 2C4G 最低配置（推荐4C8G）
- Redis 7+
- MySQL 8+（可选）
- Milvus/Qdrant（可选）

### 10.3 健康检查配置

```yaml
management:
  health:
    liveness: /actuator/health/liveness   # 存活探针
    readiness: /actuator/health/readiness # 就绪探针
    probes:
      - llm-provider      # LLM服务提供商状态
      - vector-db         # 向量数据库状态
      - redis             # Redis连接状态
      - mcp-servers       # MCP服务器聚合状态
```

---

## 11. 验收标准

### 11.1 功能验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| F1 | 快速问答 | 能够通过配置的大模型进行问答，返回正确结果 |
| F2 | 流式输出 | 支持SSE流式响应，无明显延迟 |
| F3 | 知识库问答 | 能上传文档、进行RAG问答，返回引用 |
| F4 | 任务规划 | 能将复杂任务分解为子任务并执行 |
| F5 | 推理能力 | 支持CoT/ToT/ReAct推理 |
| F6 | MCP集成 | 能连接MCP Server并调用其工具 |
| F7 | SKILL系统 | 能注册、执行内置和自定义SKILL |
| F8 | 钩子系统 | 能在各阶段插入自定义逻辑 |
| F9 | 记忆管理 | 支持会话记忆，跨会话记忆 |

### 11.2 容错验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| T1 | 熔断器 | LLM超时后自动熔断，后续请求降级 |
| T2 | 重试机制 | 临时失败自动重试，符合配置策略 |
| T3 | 限流 | 超出QPS限制的请求被拒绝 |
| T4 | 降级 | 模型不可用时自动降级到备选模型 |

### 11.3 配置验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| C1 | 配置加载 | 所有配置通过application.yml或环境变量加载 |
| C2 | 敏感信息 | API Key等敏感信息通过环境变量注入 |
| C3 | 动态生效 | 部分配置支持动态刷新 |

### 11.4 兼容性验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| K1 | 多LLM支持 | 支持至少两种LLM（OpenAI + 阿里百炼）切换 |
| K2 | 多向量库支持 | 支持至少两种向量数据库（Milvus + Qdrant）无缝切换 |
| K3 | 多存储支持 | 支持至少两种记忆存储（Redis + MySQL） |

### 11.5 安全合规验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| S1 | 认证授权 | 支持JWT/OAuth2认证，租户隔离有效 |
| S2 | 内容审核 | 输入过滤、输出审核、PII脱敏生效 |
| S3 | 审计日志 | 全量记录用户操作，可追溯 |

### 11.6 可观测性验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| O1 | 指标采集 | Prometheus采集Token用量、延迟、错误率 |
| O2 | 链路追踪 | OpenTelemetry支持请求全链路追踪 |
| O3 | 健康检查 | 多维度探针检查LLM/向量库/Redis状态 |

### 11.7 上下文与成本验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| CC1 | Token控制 | 单次请求和会话累计Token上限生效 |
| CC2 | 上下文压缩 | 达到阈值时自动触发摘要压缩 |
| CC3 | 配额管理 | 配额耗尽时阻止请求并告警 |
| CC4 | 用量统计 | 按租户/模型正确统计Token用量 |

### 11.8 缓存验收

| 编号 | 验收项 | 验收条件 |
|-----|-------|---------|
| CA1 | 响应缓存 | 相似Query返回缓存结果 |
| CA2 | 缓存淘汰 | LRU淘汰策略生效 |

---

## 12. 后续扩展建议

1. **多租户支持**：基于API Key的租户隔离
2. **成本统计**：各模型调用量与费用统计
3. **A/B测试**：模型效果对比
4. **指标监控**：Prometheus + Grafana集成
5. **分布式训练**：微调任务管理（可选）
6. **多模态扩展**：GPT-4V、Qwen-VL、Claude-3等多模态模型集成
7. **向量数据库高可用**：Milvus集群/Qdrant集群部署支持
