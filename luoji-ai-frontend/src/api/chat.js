import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1/chat',
  timeout: 60000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const sendChat = (data) => {
  return api.post('/', data)
}

export const sendStreamChat = (data) => {
  return api.post('/stream', data, {
    responseType: 'stream',
    headers: {
      'Accept': 'text/event-stream'
    }
  })
}

export const checkHealth = () => {
  return api.get('/health')
}
