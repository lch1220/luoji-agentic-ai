# 洛极 AI 智能体框架 (LuoJi AI Agent Framework)

企业级 Java AI Agent 统一中间平台，为业务系统提供智能化能力。

## 特性

### 核心能力

- **FastQA 智能问答** - 支持同步/流式输出，多轮对话上下文记忆
- **知识库问答** - RAG 架构，支持文档上传、向量检索、混合搜索
- **任务规划** - 复杂任务分解与执行，支持会话状态跟踪
- **推理引擎** - 内置 CoT / ToT / ReAct / Reflection 多种推理策略

### 技术特性

- **响应式架构** - 基于 Spring WebFlux + Project Reactor，全链路非阻塞
- **流式输出** - 支持 SSE (Server-Sent Events) 实时推送
- **工具调用** - Function Calling / Tool Use 框架，内置工具注册中心
- **MCP 集成** - MCP Client 协议支持，完整的生命周期管理
- **插件系统** - SKILL 抽象层，灵活扩展业务能力
- **记忆系统** - 会话记忆与上下文管理，支持语义搜索
- **弹性容错** - Resilience4j 熔断器 + 重试机制，保障服务稳定性

### 安全与可观测

- **JWT 认证** - 完整的身份验证与授权
- **OpenAPI 文档** - Swagger UI 自动生成
- **监控指标** - Spring Actuator + Prometheus

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Java 21 |
| 框架 | Spring Boot 3.2 / Spring WebFlux |
| AI | Spring AI 1.0 + OpenAI Adapter |
| 缓存 | Redis + Redisson |
| 存储 | MongoDB |
| 容错 | Resilience4j |
| 前端 | Vue 3 + Vite + Element Plus |
| 构建 | Maven |

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- Redis 6+
- MongoDB 5+

### 后端启动

```bash
cd luoji-ai-backend

# 配置环境变量
export OPENAI_API_KEY=your-api-key
export JWT_SECRET=your-256-bit-secret

# 编译运行
mvn spring-boot:run
```

### 前端启动

```bash
cd luoji-ai-frontend
npm install
npm run dev
```

### Docker 部署

```bash
docker-compose up -d
```

## 项目结构

```
luoji-ai/
├── luoji-ai-backend/           # Spring Boot 后端
│   └── src/main/java/com/luoji/ai/
│       ├── agent/              # Agent 实现
│       │   ├── FastQAAgent.java
│       │   ├── KnowledgeAgent.java
│       │   └── PlanningAgent.java
│       ├── core/llm/           # LLM 核心
│       │   ├── LlmClient.java
│       │   ├── ModelRouter.java
│       │   └── ReasoningEngine.java
│       ├── tools/              # 工具系统
│       ├── memory/             # 记忆系统
│       ├── hook/               # 生命周期钩子
│       └── config/             # 配置类
├── luoji-ai-frontend/          # Vue.js 前端
│   └── src/
│       ├── api/                # API 调用
│       ├── views/              # 页面视图
│       ├── stores/             # 状态管理
│       └── router/             # 路由配置
└── pom.xml                     # Maven 父项目
```

## API 接口

### 聊天 `/api/v1/chat`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/v1/chat` | 发送消息（同步） |
| POST | `/v1/chat/stream` | 发送消息（SSE 流式） |

### 知识库 `/api/v1/knowledge`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/v1/knowledge/upload/file` | 上传文档 |
| GET | `/v1/knowledge/documents` | 列出文档 |
| DELETE | `/v1/knowledge/documents/{id}` | 删除文档 |
| POST | `/v1/knowledge/query` | 知识库查询（同步） |
| POST | `/v1/knowledge/query/stream` | 知识库查询（流式） |

### Agent `/api/v1/agent`

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/v1/agent/execute` | 执行任务（同步） |
| POST | `/v1/agent/execute/stream` | 执行任务（流式） |
| GET | `/v1/agent/status/{sessionId}` | 获取执行状态 |
| POST | `/v1/agent/stop/{sessionId}` | 停止执行 |

### 工具 `/api/v1/skills`

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/v1/skills` | 列出所有技能 |
| POST | `/v1/skills/execute/{skillId}` | 执行技能 |
| POST | `/v1/skills/register` | 注册新技能 |

## 配置说明

主要环境变量：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `OPENAI_API_KEY` | OpenAI API Key | - |
| `OPENAI_BASE_URL` | API 地址 | `https://api.openai.com` |
| `JWT_SECRET` | JWT 签名密钥 | - |
| `REDIS_HOST` | Redis 主机 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `MONGODB_URI` | MongoDB 连接地址 | `mongodb://localhost:27017/luoji-ai` |

详细配置见 `luoji-ai-backend/src/main/resources/application.yml`

## 架构设计

```
                    ┌─────────────────────────────────────┐
                    │            业务系统                   │
                    └──────────────┬──────────────────────┘
                                   │
                                   ▼
┌──────────────┐    ┌──────────────────────────────────────┐
│   前端 UI    │───▶│           API Gateway                 │
└──────────────┘    └──────────────┬───────────────────────┘
                                  │
                                  ▼
                    ┌──────────────────────────────────────┐
                    │         LUOJI AI AGENT FRAMEWORK     │
                    │  ┌────────┐ ┌────────┐ ┌─────────┐  │
                    │  │FastQA │ │Knowledge│ │Planning │  │
                    │  │Agent  │ │ Agent  │ │  Agent  │  │
                    │  └────┬───┘ └────┬───┘ └────┬────┘  │
                    │       │          │          │        │
                    │  ┌────▼──────────▼──────────▼────┐   │
                    │  │         Reasoning Engine       │   │
                    │  │   CoT / ToT / ReAct / Refl    │   │
                    │  └──────────────┬────────────────┘   │
                    │                 │                    │
                    │  ┌──────────────▼────────────────┐   │
                    │  │          Tool Registry       │   │
                    │  │  web_search │ calculator │time│   │
                    │  └──────────────┬────────────────┘   │
                    │                 │                    │
                    │  ┌──────────────▼────────────────┐   │
                    │  │        Memory Manager         │   │
                    │  │   Session │ Vector │ Summary   │   │
                    │  └───────────┴─────────┴─────────┘   │
                    └──────────────────────────────────────┘
                                   │
                    ┌──────────────┼──────────────────────┐
                    ▼              ▼                       ▼
               ┌────────┐    ┌─────────┐           ┌──────────┐
               │  Redis │    │ MongoDB │           │   LLM    │
               │        │    │         │           │(OpenAI)  │
               └────────┘    └─────────┘           └──────────┘
```

## 开源协议

Apache License 2.0
