import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginForm, UserInfo } from '@/types'
import { login } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref<UserInfo | null>(null)

  const loginAction = async (loginForm: LoginForm) => {
    try {
      // 根据后端返回的LoginRES结构，直接从response中获取token
      // 因为request.ts的响应拦截器已经返回了data部分
      const response = await login(loginForm)
      token.value = response.token
      // 设置用户信息，提供完整的UserInfo类型属性
      userInfo.value = {
        id: response.id,
        username: response.username,
        nickname: '', // 提供默认值
        email: '', // 提供默认值
        phone: '', // 提供默认值
        avatar: '', // 提供默认值
        roles: [], // 提供默认值
        permissions: [] // 提供默认值
      }
      localStorage.setItem('token', response.token)
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