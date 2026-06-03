<template>
  <div class="knowledge-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>知识库管理</h1>
          <div class="header-actions">
            <el-button type="primary" @click="showUploadDialog = true">
              <el-icon><Upload /></el-icon>
              上传文档
            </el-button>
            <el-button @click="rebuildIndex">
              <el-icon><Refresh /></el-icon>
              重建索引
            </el-button>
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
            <el-col :span="8">
              <el-card>
                <template #header>
                  <span>文档列表</span>
                </template>
                <div v-if="documents.length === 0" class="empty-state">
                  <el-empty description="暂无文档" />
                </div>
                <el-space v-else direction="vertical" style="width: 100%">
                  <div v-for="doc in documents" :key="doc.id" class="document-item">
                    <div class="doc-info">
                      <el-icon><Document /></el-icon>
                      <span>{{ doc.fileName }}</span>
                    </div>
                    <el-button type="danger" size="small" @click="deleteDoc(doc.id)">
                      删除
                    </el-button>
                  </div>
                </el-space>
              </el-card>
            </el-col>

            <el-col :span="16">
              <el-card>
                <template #header>
                  <span>RAG问答</span>
                </template>
                <div class="query-section">
                  <el-input
                    v-model="queryText"
                    type="textarea"
                    :rows="3"
                    placeholder="输入您的问题..."
                  />
                  <el-button
                    type="primary"
                    :loading="queryLoading"
                    style="margin-top: 10px"
                    @click="queryKnowledge"
                  >
                    查询
                  </el-button>
                </div>

                <div v-if="queryResult" class="result-section">
                  <el-divider>查询结果</el-divider>
                  <div class="result-content">{{ queryResult }}</div>

                  <template v-if="sources.length > 0">
                    <el-divider>参考来源</el-divider>
                    <div v-for="(source, idx) in sources" :key="idx" class="source-item">
                      <el-tag>{{ source.fileName }}</el-tag>
                      <span class="source-chunk">{{ source.chunk }}</span>
                    </div>
                  </template>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-main>
      </el-container>
    </el-container>

    <el-dialog v-model="showUploadDialog" title="上传文档" width="500px">
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="上传方式">
          <el-radio-group v-model="uploadMode">
            <el-radio label="file">本地文件</el-radio>
            <el-radio label="content">手动输入</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="uploadMode === 'file'">
          <el-form-item label="选择文件">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :file-list="fileList"
              accept=".txt,.md,.pdf,.docx"
            >
              <el-button type="primary">选择文件</el-button>
              <template #tip>
                <div class="el-upload__tip">支持 txt, md, pdf, docx 格式</div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="uploadForm.category" placeholder="请输入分类" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="文件名">
            <el-input v-model="uploadForm.fileName" placeholder="请输入文件名" />
          </el-form-item>
          <el-form-item label="文档类型">
            <el-select v-model="uploadForm.fileType" placeholder="选择类型">
              <el-option label="TXT" value="txt" />
              <el-option label="Markdown" value="md" />
              <el-option label="PDF" value="pdf" />
              <el-option label="Word" value="docx" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="uploadForm.category" placeholder="请输入分类" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input
              v-model="uploadForm.content"
              type="textarea"
              :rows="6"
              placeholder="请输入文档内容"
            />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listDocuments, deleteDocument, uploadDocument, uploadFile, queryKnowledge as queryApi, rebuildIndex as rebuildIndexApi } from '@/api/knowledge'
import { HomeFilled, ChatDotRound, Document, MagicStick, Setting, Upload, Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = computed(() => router.currentRoute.value.path)

const documents = ref([])
const queryText = ref('')
const queryResult = ref('')
const sources = ref([])
const queryLoading = ref(false)
const showUploadDialog = ref(false)
const uploadMode = ref('file')
const fileList = ref([])
const uploadRef = ref(null)
const selectedFile = ref(null)

const uploadForm = reactive({
  fileName: '',
  fileType: 'txt',
  category: '',
  content: ''
})

const loadDocuments = async () => {
  try {
    const response = await listDocuments()
    documents.value = response.data
  } catch (error) {
    ElMessage.error('加载文档失败')
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleUpload = async () => {
  try {
    if (uploadMode.value === 'file') {
      if (!selectedFile.value) {
        ElMessage.warning('请选择文件')
        return
      }
      await uploadFile(selectedFile.value, uploadForm.category)
    } else {
      await uploadDocument(uploadForm)
    }
    ElMessage.success('上传成功')
    showUploadDialog.value = false
    resetUploadForm()
    loadDocuments()
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const resetUploadForm = () => {
  uploadForm.fileName = ''
  uploadForm.fileType = 'txt'
  uploadForm.category = ''
  uploadForm.content = ''
  selectedFile.value = null
  fileList.value = []
  uploadMode.value = 'file'
}

const deleteDoc = async (id) => {
  try {
    await deleteDocument(id)
    ElMessage.success('删除成功')
    loadDocuments()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const queryKnowledge = async () => {
  if (!queryText.value.trim()) return

  queryLoading.value = true
  queryResult.value = ''
  sources.value = []

  try {
    const response = await queryApi({
      query: queryText.value
    })
    queryResult.value = response.data.answer
    sources.value = response.data.sources || []
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    queryLoading.value = false
  }
}

const rebuildIndex = async () => {
  try {
    await rebuildIndexApi()
    ElMessage.success('索引重建成功')
  } catch (error) {
    ElMessage.error('索引重建失败')
  }
}

loadDocuments()
</script>

<style scoped>
.knowledge-container {
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
  gap: 10px;
}

.el-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}

.el-main {
  background: #f5f7fa;
}

.document-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.doc-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.query-section {
  margin-bottom: 20px;
}

.result-section {
  margin-top: 20px;
}

.result-content {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
}

.source-item {
  display: flex;
  gap: 10px;
  padding: 8px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.source-chunk {
  flex: 1;
  font-size: 13px;
  color: #666;
}
</style>
