import { defineStore } from 'pinia'
import { ref } from 'vue'
import { sendChat, sendStreamChat } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const isLoading = ref(false)
  const isStreaming = ref(false)
  const sessionId = ref('')

  let eventSource = null

  async function sendMessage(content) {
    if (!content.trim()) return

    // Add user message
    messages.value.push({
      id: Date.now(),
      role: 'user',
      content,
      timestamp: new Date()
    })

    isLoading.value = true

    try {
      const response = await sendChat({
        message: content,
        sessionId: sessionId.value
      })

      messages.value.push({
        id: Date.now() + 1,
        role: 'assistant',
        content: response.data.message,
        timestamp: new Date()
      })

      sessionId.value = response.data.sessionId || sessionId.value
    } catch (error) {
      messages.value.push({
        id: Date.now() + 1,
        role: 'assistant',
        content: '抱歉，发生了错误：' + error.message,
        timestamp: new Date(),
        isError: true
      })
    } finally {
      isLoading.value = false
    }
  }

  async function sendMessageStream(content) {
    if (!content.trim()) return

    messages.value.push({
      id: Date.now(),
      role: 'user',
      content,
      timestamp: new Date()
    })

    const assistantMessage = {
      id: Date.now() + 1,
      role: 'assistant',
      content: '',
      timestamp: new Date(),
      isStreaming: true
    }
    messages.value.push(assistantMessage)

    isStreaming.value = true

    try {
      const response = await sendStreamChat({
        message: content,
        sessionId: sessionId.value
      })

      // Handle SSE stream
      const reader = response.data.getReader()
      const decoder = new TextDecoder()

      while (true) {
        const { done, value } = await reader.read()
        if (done) break

        const chunk = decoder.decode(value)
        assistantMessage.content += chunk
      }

      assistantMessage.isStreaming = false
      sessionId.value = response.headers.sessionid || sessionId.value
    } catch (error) {
      assistantMessage.content = '抱歉，发生了错误：' + error.message
      assistantMessage.isStreaming = false
      assistantMessage.isError = true
    } finally {
      isStreaming.value = false
    }
  }

  function clearMessages() {
    messages.value = []
    sessionId.value = ''
  }

  return {
    messages,
    isLoading,
    isStreaming,
    sessionId,
    sendMessage,
    sendMessageStream,
    clearMessages
  }
})
