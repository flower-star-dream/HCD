import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginForm, UserInfo } from '@/types'
import { login } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref<UserInfo | null>(null)

  const loginAction = async (loginForm: LoginForm) => {
    try {
      const response = await login(loginForm)
      token.value = response.data.token
      userInfo.value = response.data.user
      localStorage.setItem('token', response.data.token)
      return response
    } catch (error) {
      throw error
    }
  }

  const logoutAction = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    loginAction,
    logoutAction
  }
})