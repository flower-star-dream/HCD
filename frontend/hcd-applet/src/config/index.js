// 环境变量
const env = process.env.NODE_ENV || 'development'

// 环境配置
const configs = {
  development: {
    baseUrl: 'http://localhost:8080',
    ossUrl: 'https://dev-oss.example.com',
    apiPrefix: '/api',
    timeout: 10000,
    mock: true,
    debug: true,
    title: '火车订票小程序 - 开发环境',
    version: '1.0.0',
  },
  staging: {
    baseUrl: 'https://staging-api.example.com',
    ossUrl: 'https://staging-oss.example.com',
    apiPrefix: '/api',
    timeout: 10000,
    mock: false,
    debug: true,
    title: '火车订票小程序 - 测试环境',
    version: '1.0.0',
  },
  production: {
    baseUrl: 'https://api.example.com',
    ossUrl: 'https://oss.example.com',
    apiPrefix: '/api',
    timeout: 5000,
    mock: false,
    debug: false,
    title: '火车订票小程序',
    version: '1.0.0',
  },
}

// 获取当前环境配置
const config = configs[env] || configs.development

// 导出环境类型
const isDev = env === 'development'
const isStaging = env === 'staging'
const isProd = env === 'production'

// 平台判断
const platform = (function() {
  // #ifdef H5
  return 'h5'
  // #endif
  // #ifdef MP-WEIXIN
  return 'mp-weixin'
  // #endif
  // #ifdef MP-ALIPAY
  return 'mp-alipay'
  // #endif
  // #ifdef APP-PLUS
  return 'app-plus'
  // #endif
  return 'unknown'
})()

// 导出配置
export { config, isDev, isStaging, isProd, platform }
export default config