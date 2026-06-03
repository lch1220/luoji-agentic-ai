<template>
  <div class="chat-window">
    <div class="chat-messages" ref="messagesContainer">
      <message-list :messages="chatStore.messages" />
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="3"
        placeholder="输入您的问题，按Enter发送..."
        @keyup.enter.ctrl="handleSend"
        resize="none"
      />
      <div class="input-actions">
        <span class="hint">Ctrl + Enter 发送</span>
        <el-button
          type="primary"
          :loading="chatStore.isLoading || chatStore.isStreaming"
          @click="handleSend"
        >
          {{ chatStore.isStreaming ? '生成中...' : '发送' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useChatStore } from '@/stores/chat'
import MessageList from './MessageList.vue'

const chatStore = useChatStore()
const inputMessage = ref('')
const messagesContainer = ref(null)

const handleSend = async () => {
  if (!inputMessage.value.trim()) return
  if (chatStore.isLoading || chatStore.isStreaming) return

  const message = inputMessage.value
  inputMessage.value = ''

  await chatStore.sendMessageStream(message)
}

watch(() => chatStore.messages.length, () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
})
</script>

<style scoped>
.chat-window {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.chat-input {
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.hint {
  font-size: 12px;
  color: #999;
}
</style>
