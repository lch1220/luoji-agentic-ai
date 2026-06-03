<template>
  <div class="settings-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>系统设置</h1>
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
          <el-card>
            <template #header>
              <span>LLM配置</span>
            </template>
            <el-form :model="llmConfig" label-width="120px">
              <el-form-item label="默认模型">
                <el-select v-model="llmConfig.defaultModel">
                  <el-option label="GPT-4o" value="gpt-4o" />
                  <el-option label="GPT-4o-mini" value="gpt-4o-mini" />
                  <el-option label="GPT-4 Turbo" value="gpt-4-turbo" />
                </el-select>
              </el-form-item>
              <el-form-item label="温度参数">
                <el-slider v-model="llmConfig.temperature" :min="0" :max="2" :step="0.1" />
                <span>{{ llmConfig.temperature }}</span>
              </el-form-item>
              <el-form-item label="最大Token数">
                <el-input-number v-model="llmConfig.maxTokens" :min="100" :max="32000" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary">保存配置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <el-card style="margin-top: 20px">
            <template #header>
              <span>Agent配置</span>
            </template>
            <el-form :model="agentConfig" label-width="120px">
              <el-form-item label="启用CoT">
                <el-switch v-model="agentConfig.enableCoT" />
              </el-form-item>
              <el-form-item label="启用ToT">
                <el-switch v-model="agentConfig.enableToT" />
              </el-form-item>
              <el-form-item label="最大规划步数">
                <el-input-number v-model="agentConfig.maxSteps" :min="1" :max="20" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary">保存配置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { HomeFilled, ChatDotRound, Document, MagicStick, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = computed(() => router.currentRoute.value.path)

const llmConfig = reactive({
  defaultModel: 'gpt-4o',
  temperature: 0.7,
  maxTokens: 4096
})

const agentConfig = reactive({
  enableCoT: true,
  enableToT: false,
  maxSteps: 10
})
</script>

<style scoped>
.settings-container {
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
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.el-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.el-main {
  background: #f5f7fa;
}
</style>
