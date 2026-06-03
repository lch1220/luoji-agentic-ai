<template>
  <div class="message-list">
    <div v-if="messages.length === 0" class="empty-state">
      <el-empty description="暂无消息，开始对话吧！">
        <el-button type="primary" @click="showSuggestions = true">发送示例消息</el-button>
      </el-empty>

      <div v-if="showSuggestions" class="suggestions">
        <el-card>
          <template #header>示例问题</template>
          <el-space wrap>
            <el-tag
              v-for="suggestion in suggestions"
              :key="suggestion"
              class="suggestion-tag"
              @click="emitSuggestion(suggestion)"
            >
              {{ suggestion }}
            </el-tag>
          </el-space>
        </el-card>
      </div>
    </div>

    <div v-else>
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message-item', `message-${message.role}`]"
      >
        <div class="message-avatar">
          <el-avatar :icon="message.role === 'user' ? 'User' : 'ChatDotRound'" />
        </div>
        <div class="message-content">
          <div class="message-header">
            <span class="message-role">{{ message.role === 'user' ? '我' : 'AI助手' }}</span>
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
          </div>
          <div class="message-bubble" :class="{ 'is-error': message.isError, 'is-streaming': message.isStreaming }">
            <template v-if="message.isStreaming">
              {{ message.content }}<span class="typing-indicator">...</span>
            </template>
            <template v-else>
              {{ message.content }}
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['send'])

const showSuggestions = ref(false)

const suggestions = [
  '你好，请介绍一下你自己',
  '什么是人工智能？',
  '帮我写一段Python代码',
  '解释一下机器学习'
]

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const emitSuggestion = (suggestion) => {
  emit('send', suggestion)
}
</script>

<style scoped>
.message-list {
  max-width: 900px;
  margin: 0 auto;
}

.empty-state {
  text-align: center;
  padding-top: 100px;
}

.suggestions {
  margin-top: 20px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.suggestion-tag {
  cursor: pointer;
}

.message-item {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.message-user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.message-user .message-content {
  text-align: right;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.message-user .message-header {
  flex-direction: row-reverse;
}

.message-role {
  font-weight: bold;
  font-size: 14px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-bubble {
  display: inline-block;
  padding: 12px 16px;
  border-radius: 12px;
  background: #f0f2f5;
  white-space: pre-wrap;
  word-break: break-word;
  max-width: 100%;
}

.message-user .message-bubble {
  background: #409eff;
  color: #fff;
}

.message-bubble.is-error {
  background: #fef0f0;
  color: #f56c6c;
}

.message-bubble.is-streaming {
  background: #f0f9eb;
  color: #67c23a;
}

.typing-indicator {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
</style>
