<template>
  <div class="chat-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>智能问答</h1>
          <div class="header-actions">
            <el-switch
              v-model="streamingMode"
              active-text="流式输出"
              inactive-text="普通模式"
            />
            <el-button @click="clearChat">清空对话</el-button>
          </div>
        </div>
      </el-header>

      <el-container>
        <el-aside width="200px">
          <el-menu :default-active="activeMenu" router>
            <el-menu-item index="/">
              <el-icon><HomeFilled /></el-icon>
              <span>控制台</span>
            </el-menu-item>
            <el-menu-item index="/chat">
              <el-icon><ChatDotRound /></el-icon>
              <span>聊天</span>
            </el-menu-item>
            <el-menu-item index="/knowledge">
              <el-icon><Document /></el-icon>
              <span>知识库</span>
            </el-menu-item>
            <el-menu-item index="/skills">
              <el-icon><MagicStick /></el-icon>
              <span>技能</span>
            </el-menu-item>
            <el-menu-item index="/settings">
              <el-icon><Setting /></el-icon>
              <span>设置</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <el-main>
          <chat-window />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'
import ChatWindow from '@/components/ChatWindow.vue'
import { HomeFilled, ChatDotRound, Document, MagicStick, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const chatStore = useChatStore()

const activeMenu = computed(() => router.currentRoute.value.path)
const streamingMode = ref(true)

const clearChat = () => {
  chatStore.clearMessages()
}
</script>

<style scoped>
.chat-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.el-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.el-main {
  padding: 0;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}
</style>
