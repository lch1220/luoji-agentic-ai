<template>
  <div class="dashboard-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>洛极AI智能体</h1>
          <div class="header-actions">
            <span class="username">{{ userStore.username }}</span>
            <el-button @click="handleLogout">退出</el-button>
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
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card>
                <div class="stat-card">
                  <div class="stat-icon" style="background: #409eff">
                    <el-icon><ChatDotRound /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.totalChats }}</div>
                    <div class="stat-label">对话总数</div>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="6">
              <el-card>
                <div class="stat-card">
                  <div class="stat-icon" style="background: #67c23a">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.totalDocuments }}</div>
                    <div class="stat-label">文档数量</div>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="6">
              <el-card>
                <div class="stat-card">
                  <div class="stat-icon" style="background: #e6a23c">
                    <el-icon><MagicStick /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.totalSkills }}</div>
                    <div class="stat-label">可用技能</div>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="6">
              <el-card>
                <div class="stat-card">
                  <div class="stat-icon" style="background: #f56c6c">
                    <el-icon><Timer /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.activeSessions }}</div>
                    <div class="stat-label">活跃会话</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="12">
              <el-card>
                <template #header>
                  <span>最近对话</span>
                </template>
                <el-empty description="暂无对话记录" />
              </el-card>
            </el-col>

            <el-col :span="12">
              <el-card>
                <template #header>
                  <span>系统状态</span>
                </template>
                <el-space direction="vertical" style="width: 100%">
                  <el-tag type="success">后端服务: 在线</el-tag>
                  <el-tag type="success">LLM服务: 在线</el-tag>
                  <el-tag type="success">数据库: 在线</el-tag>
                </el-space>
              </el-card>
            </el-col>
          </el-row>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { HomeFilled, ChatDotRound, Document, MagicStick, Setting, Timer } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => router.currentRoute.value.path)

const stats = ref({
  totalChats: 0,
  totalDocuments: 0,
  totalSkills: 3,
  activeSessions: 0
})

const handleLogout = async () => {
  await userStore.logoutAction()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container {
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

.username {
  color: #666;
}

.el-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.el-main {
  padding: 20px;
  background: #f5f7fa;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}
</style>
