<template>
  <div class="skills-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>技能管理</h1>
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
            <el-col v-for="skill in skills" :key="skill.id" :span="8" style="margin-bottom: 20px">
              <el-card class="skill-card">
                <template #header>
                  <div class="skill-header">
                    <el-icon size="24"><MagicStick /></el-icon>
                    <span>{{ skill.name }}</span>
                  </div>
                </template>
                <div class="skill-body">
                  <p class="skill-desc">{{ skill.description }}</p>
                  <el-tag size="small">{{ skill.category }}</el-tag>
                </div>
                <template #footer>
                  <el-button type="primary" @click="executeSkill(skill)">执行</el-button>
                </template>
              </el-card>
            </el-col>
          </el-row>
        </el-main>
      </el-container>
    </el-container>

    <el-dialog v-model="showExecuteDialog" title="执行技能" width="500px">
      <el-form :model="executeForm" label-width="100px">
        <el-form-item label="技能名称">
          <span>{{ executeForm.name }}</span>
        </el-form-item>
        <el-form-item
          v-for="param in executeForm.parameters"
          :key="param.name"
          :label="param.name"
        >
          <el-input v-model="param.value" :placeholder="param.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showExecuteDialog = false">取消</el-button>
        <el-button type="primary" :loading="executing" @click="confirmExecute">执行</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showResultDialog" title="执行结果" width="500px">
      <div class="result-content">{{ executionResult }}</div>
      <template #footer>
        <el-button @click="showResultDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listSkills, executeSkill as executeSkillApi } from '@/api/skills'
import { HomeFilled, ChatDotRound, Document, MagicStick, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = computed(() => router.currentRoute.value.path)

const skills = ref([])
const showExecuteDialog = ref(false)
const showResultDialog = ref(false)
const executing = ref(false)
const executionResult = ref('')

const executeForm = reactive({
  id: '',
  name: '',
  parameters: []
})

const loadSkills = async () => {
  try {
    const response = await listSkills()
    console.log('Skills response:', response)
    skills.value = response.data
  } catch (error) {
    console.error('Load skills error:', error)
    ElMessage.error('加载技能失败: ' + (error.response?.data?.message || error.message))
  }
}

const executeSkill = (skill) => {
  executeForm.id = skill.id
  executeForm.name = skill.name
  executeForm.parameters = (skill.parameters || []).map(p => ({
    name: p.name,
    description: p.description,
    value: p.defaultValue || ''
  }))
  showExecuteDialog.value = true
}

const confirmExecute = async () => {
  executing.value = true
  const params = {}
  executeForm.parameters.forEach(p => {
    if (p.value) {
      params[p.name] = p.value
    }
  })

  try {
    const response = await executeSkillApi(executeForm.id, params)
    executionResult.value = response.data.result || JSON.stringify(response.data, null, 2)
    showExecuteDialog.value = false
    showResultDialog.value = true
  } catch (error) {
    ElMessage.error('执行失败：' + error.message)
  } finally {
    executing.value = false
  }
}

onMounted(() => {
  loadSkills()
})
</script>

<style scoped>
.skills-container {
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

.skill-card {
  height: 100%;
}

.skill-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.skill-body {
  min-height: 80px;
}

.skill-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.result-content {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
  font-family: monospace;
}
</style>
