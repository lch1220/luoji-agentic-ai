import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1/agent',
  timeout: 300000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const executeTask = (data) => {
  return api.post('/execute', data)
}

export const executeTaskStream = (data) => {
  return api.post('/execute/stream', data, {
    responseType: 'stream',
    headers: {
      'Accept': 'text/event-stream'
    }
  })
}

export const getSessionStatus = (sessionId) => {
  return api.get(`/status/${sessionId}`)
}

export const stopSession = (sessionId) => {
  return api.post(`/stop/${sessionId}`)
}

export const getHistory = (sessionId) => {
  return api.get(`/history/${sessionId}`)
}
