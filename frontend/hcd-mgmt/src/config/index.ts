import type { Config } from './types'
<<<<<<< HEAD

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
=======
import { getEnv } from './env'

/**
 * 统一配置管理模块
 * 将所有环境配置集中在一处，无需修改.env文件
 */

// 获取当前环境
const env = getEnv()

// 服务模块配置常量
const serviceConfig = {
  // 开发环境服务配置 - 路径为空，由gateway处理路由
  development: {
    user: '',
    trainSeat: '',
    ticket: '',
    order: '',
    system: ''
  },
  // 测试环境服务配置 - 路径为空，由gateway处理路由
  staging: {
    user: '',
    trainSeat: '',
    ticket: '',
    order: '',
    system: ''
  },
  // 生产环境服务配置 - 路径为空，由gateway处理路由
  production: {
    user: '',
    trainSeat: '',
    ticket: '',
    order: '',
    system: ''
  }
}

/**
 * 主配置对象 - 包含所有环境的默认配置
 * 如需修改配置，只需修改此对象中的对应值
 */
const defaultConfig: Config = {
  baseUrl: 'http://localhost:8080',
  ossUrl: 'http://localhost:9001',
  apiPrefix: '/api/v1/mgmt',
  timeout: 10000,
  mock: false,
  debug: true,
  title: '火车订票系统-后端管理',
  services: serviceConfig.development
}

/**
 * 环境特定配置覆盖
 * 用于定义不同环境的特定配置
 */
const envConfigs: Record<string, Partial<Config>> = {
  // 开发环境特定配置
  development: {
    title: '火车订票系统-后端管理 - 开发环境',
    services: serviceConfig.development
  },
  
  // 测试环境特定配置
  staging: {
    baseUrl: 'https://staging-api.hcd.com',
    ossUrl: 'https://staging-oss.hcd.com',
    mock: false,
    debug: true,
    title: '火车订票系统-后端管理 - 测试环境',
    services: serviceConfig.staging
  },
  
  // 生产环境特定配置
  production: {
    baseUrl: 'https://api.hcd.com',
    ossUrl: 'https://oss.hcd.com',
    timeout: 5000,
    mock: false,
    debug: false,
    services: serviceConfig.production
  }
}

// 合并默认配置和环境特定配置
export const config: Config = {
  ...defaultConfig,
  ...(envConfigs[env] || {})
}

// 环境判断辅助函数
>>>>>>> 7194a667e73e05f6f820be501adf75d935dc6a3c
export const isDev = env === 'development'
export const isStaging = env === 'staging'
export const isProd = env === 'production'

// 默认导出配置
export default config