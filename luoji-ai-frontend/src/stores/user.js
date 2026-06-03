import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, validateToken } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => roles.value.includes('ROLE_ADMIN'))

  async function loginAction(credentials) {
    try {
      const response = await login(credentials)
      token.value = response.data.token
      username.value = response.data.username
      roles.value = response.data.roles

      localStorage.setItem('token', token.value)
      localStorage.setItem('username', username.value)
      localStorage.setItem('roles', JSON.stringify(roles.value))

      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  async function logoutAction() {
    try {
      await logout()
    } finally {
      token.value = ''
      username.value = ''
      roles.value = []
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('roles')
    }
  }

  async function validateTokenAction() {
    if (!token.value) return false
    try {
      const response = await validateToken(token.value)
      return response.data
    } catch {
      return false
    }
  }

  return {
    token,
    username,
    roles,
    isLoggedIn,
    isAdmin,
    loginAction,
    logoutAction,
    validateTokenAction
  }
})
