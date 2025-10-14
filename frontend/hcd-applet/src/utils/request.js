import { useUserStore } from '@/store/user'

const baseURL = 'http://localhost:8080'

class Request {
  constructor() {
    this.baseURL = baseURL
    this.timeout = 10000
  }

  request(options = {}) {
    return new Promise((resolve, reject) => {
      const {
        url,
        method = 'GET',
        data = {},
        header = {},
        showLoading = true,
        ...otherOptions
      } = options

      if (showLoading) {
        uni.showLoading({
          title: '加载中...',
          mask: true
        })
      }

      const token = uni.getStorageSync('token')
      if (token) {
        header.Authorization = `Bearer ${token}`
      }

      uni.request({
        url: this.baseURL + url,
        method,
        data,
        header: {
          'Content-Type': 'application/json',
          ...header
        },
        timeout: this.timeout,
        ...otherOptions,
        success: (res) => {
          const { data } = res

          if (data.code === 200) {
            resolve(data.data)
          } else if (data.code === 401) {
            const userStore = useUserStore()
            userStore.logoutAction()
            uni.showToast({
              title: '登录已过期，请重新登录',
              icon: 'none'
            })
            uni.navigateTo({
              url: '/pages/user/login'
            })
            reject(data)
          } else {
            uni.showToast({
              title: data.message || '请求失败',
              icon: 'none'
            })
            reject(data)
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(err)
        },
        complete: () => {
          if (showLoading) {
            uni.hideLoading()
          }
        }
      })
    })
  }

  get(url, params = {}, options = {}) {
    return this.request({
      url,
      method: 'GET',
      data: params,
      ...options
    })
  }

  post(url, data = {}, options = {}) {
    return this.request({
      url,
      method: 'POST',
      data,
      ...options
    })
  }

  put(url, data = {}, options = {}) {
    return this.request({
      url,
      method: 'PUT',
      data,
      ...options
    })
  }

  delete(url, data = {}, options = {}) {
    return this.request({
      url,
      method: 'DELETE',
      data,
      ...options
    })
  }
}

const request = new Request()

export default request