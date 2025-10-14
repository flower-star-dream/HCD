import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)

  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      token.value = response.token
      userInfo.value = response.user
      uni.setStorageSync('token', response.token)
      return response
    } catch (error) {
      throw error
    }
  }

  const logoutAction = () => {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
  }

  const initUserInfo = async () => {
    const storedToken = uni.getStorageSync('token')
    if (storedToken) {
      token.value = storedToken
      try {
        const info = await getUserInfo()
        userInfo.value = info
      } catch (error) {
        // Token 过期或无效
        logoutAction()
      }
    }
  }

  const isLogin = computed(() => !!token.value)

  return {
    token,
    userInfo,
    isLogin,
    loginAction,
    logoutAction,
    initUserInfo
  }
})