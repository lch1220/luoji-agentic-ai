import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1/auth',
  timeout: 30000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const login = (credentials) => {
  return api.post('/login', credentials)
}

export const logout = () => {
  return api.get('/logout')
}

export const validateToken = (token) => {
  return api.post('/validate', {}, {
    headers: { Authorization: `Bearer ${token}` }
  })
}
