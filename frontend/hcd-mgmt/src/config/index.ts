import type { Config } from './types'

// 环境变量
const env = import.meta.env.MODE || process.env.NODE_ENV || 'development'

// 环境配置
const configs: Record<string, Config> = {
  development: {
    baseUrl: 'http://localhost:8080',
    ossUrl: 'https://dev-oss.example.com',
    apiPrefix: '/api',
    timeout: 10000,
    mock: true,
    debug: true,
    title: '火车订票系统 - 开发环境',
  },
  staging: {
    baseUrl: 'https://staging-api.example.com',
    ossUrl: 'https://staging-oss.example.com',
    apiPrefix: '/api',
    timeout: 10000,
    mock: false,
    debug: true,
    title: '火车订票系统 - 测试环境',
  },
  production: {
    baseUrl: 'https://api.example.com',
    ossUrl: 'https://oss.example.com',
    apiPrefix: '/api',
    timeout: 5000,
    mock: false,
    debug: false,
    title: '火车订票系统',
  },
}

// 获取当前环境配置
export const config = configs[env] || configs.development

// 导出环境类型
export const isDev = env === 'development'
export const isStaging = env === 'staging'
export const isProd = env === 'production'

// 默认导出配置
export default config