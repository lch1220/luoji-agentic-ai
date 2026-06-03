import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1/skills',
  timeout: 30000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const listSkills = () => {
  return api.get('')
}

export const getSkill = (skillId) => {
  return api.get(`/${skillId}`)
}

export const executeSkill = (skillId, parameters) => {
  return api.post(`/execute/${skillId}`, { parameters })
}

export const registerSkill = (skillInfo) => {
  return api.post('/register', skillInfo)
}

export const unregisterSkill = (skillId) => {
  return api.delete(`/${skillId}`)
}
