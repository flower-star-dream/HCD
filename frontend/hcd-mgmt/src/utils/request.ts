import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'
import config from '@/config'

// 请求拦截器
import type { InternalAxiosRequestConfig } from 'axios'

const requestInterceptor = (config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    // 添加Bearer前缀，确保token格式正确
    config.headers.Authorization = `Bearer ${token}`
    // 添加必需的biz_side头部，设置为admin，使用小写格式以匹配后端期望
    config.headers.biz_side = 'admin'
  }
  return config
}

// 响应拦截器
const responseInterceptor = (response: AxiosResponse<ApiResponse>) => {
  const { code, message, data } = response.data

  if (code === 200) {
    return data
  } else {
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  }
}

// 错误响应拦截器
const errorResponseInterceptor = (error: any) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('token')
    window.location.href = '/login'
    ElMessage.error('登录已过期，请重新登录')
  } else {
    ElMessage.error(error.response?.data?.message || '网络错误')
  }
  return Promise.reject(error)
}

// 创建请求实例并应用拦截器
const createRequestInstance = (baseURL: string) => {
  const instance = axios.create({
    baseURL,
    timeout: config.timeout
  })
  
  instance.interceptors.request.use(requestInterceptor, error => Promise.reject(error))
  instance.interceptors.response.use(responseInterceptor, errorResponseInterceptor)
  
  return instance
}

// 默认请求实例
const request = createRequestInstance(`${config.baseUrl}${config.apiPrefix}`)

/**
 * 根据服务模块创建请求方法
 * @param module 服务模块名称
 * @returns 特定模块的请求方法
 * 注意：根据网关配置，路径格式为 ${baseUrl}/${apiPrefix}/${module}
 */
export const createServiceRequest = (module: keyof typeof config.services) => {
  return createRequestInstance(`${config.baseUrl}${config.apiPrefix}/${module}`)
}

// 导出各个服务模块的请求实例
export const userRequest = createServiceRequest('user')
export const trainSeatRequest = createServiceRequest('trainSeat')
export const ticketRequest = createServiceRequest('ticket')
export const orderRequest = createServiceRequest('order')
export const systemRequest = createServiceRequest('system')

export default request