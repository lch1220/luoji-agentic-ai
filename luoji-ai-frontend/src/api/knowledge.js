import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1/knowledge',
  timeout: 60000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const uploadDocument = (data) => {
  return api.post('/upload', data)
}

export const uploadFile = (file, category = 'default') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('category', category)
  return api.post('/upload/file', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const listDocuments = () => {
  return api.get('/documents')
}

export const deleteDocument = (id) => {
  return api.delete(`/documents/${id}`)
}

export const queryKnowledge = (data) => {
  return api.post('/query', data)
}

export const queryKnowledgeStream = (data) => {
  return api.post('/query/stream', data, {
    responseType: 'stream',
    headers: {
      'Accept': 'text/event-stream'
    }
  })
}

export const rebuildIndex = () => {
  return api.post('/index')
}
