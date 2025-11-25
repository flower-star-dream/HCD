import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, wechatLogin, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)

  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      token.value = response.token
      userInfo.value = response.user
      uni.setStorageSync('token', response.token)
      if (response.user) {
        uni.setStorageSync('userInfo', JSON.stringify(response.user))
      }
      return response
    } catch (error) {
      throw error
    }
  }

  const wechatLoginAction = async (wechatData) => {
    try {
      const response = await wechatLogin(wechatData)
      token.value = response.token
      userInfo.value = response.user
      uni.setStorageSync('token', response.token)
      if (response.user) {
        uni.setStorageSync('userInfo', JSON.stringify(response.user))
      }
      return response
    } catch (error) {
      throw error
    }
  }

  const logoutAction = () => {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  const initUserInfo = async () => {
    const storedToken = uni.getStorageSync('token')
    const storedUserInfo = uni.getStorageSync('userInfo')
    
    if (storedToken) {
      token.value = storedToken
      if (storedUserInfo) {
        try {
          userInfo.value = JSON.parse(storedUserInfo)
        } catch (error) {
          userInfo.value = null
        }
      }
      
      try {
        // 尝试从服务器获取最新用户信息
        const info = await getUserInfo()
        userInfo.value = info
        uni.setStorageSync('userInfo', JSON.stringify(info))
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
    wechatLoginAction,
    logoutAction,
    initUserInfo
  }
})