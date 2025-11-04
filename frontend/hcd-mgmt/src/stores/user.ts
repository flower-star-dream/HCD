import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginForm, UserInfo } from '@/types/user'
import { login } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 初始化时从localStorage读取数据
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>()

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
        phone: '', // 提供默认值
        avatar: '', // 提供默认值
        affiliatedSite: '', // 提供默认值
        permissionLevel: '' // 提供默认值
      }
      // 手动保存到localStorage
      localStorage.setItem('token', response.token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return response
    } catch (error) {
      throw error
    }
  }

  const setUserInfo = (newUserInfo: UserInfo) => {
    userInfo.value = newUserInfo
    // 手动保存到localStorage
    if (newUserInfo) {
      localStorage.setItem('userInfo', JSON.stringify(newUserInfo))
    }
  }

  const logoutAction = () => {
    token.value = ''
    userInfo.value = null
    // 手动从localStorage移除
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    setUserInfo,
    loginAction,
    logoutAction
  }
})

export default useUserStore