// 环境工具函数

/**
 * 获取当前环境
 */
export const getEnv = () => {
  return process.env.NODE_ENV || 'development'
}

/**
 * 判断是否为开发环境
 */
export const isDev = () => {
  return getEnv() === 'development'
}

/**
 * 判断是否为测试环境
 */
export const isStaging = () => {
  return getEnv() === 'staging'
}

/**
 * 判断是否为生产环境
 */
export const isProd = () => {
  return getEnv() === 'production'
}

/**
 * 获取环境变量值
 */
export const getEnvVar = (key, defaultValue = '') => {
  return process.env[key] || defaultValue
}

/**
 * 获取布尔类型的环境变量
 */
export const getBooleanEnvVar = (key, defaultValue = false) => {
  const value = getEnvVar(key)
  if (value === '') return defaultValue
  return value === 'true'
}

/**
 * 获取数字类型的环境变量
 */
export const getNumberEnvVar = (key, defaultValue = 0) => {
  const value = getEnvVar(key)
  if (value === '') return defaultValue
  const num = Number(value)
  return isNaN(num) ? defaultValue : num
}

/**
 * 获取平台信息
 */
export const getPlatform = () => {
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
}

/**
 * 判断是否为H5平台
 */
export const isH5 = () => {
  // #ifdef H5
  return true
  // #endif
  return false
}

/**
 * 判断是否为微信小程序
 */
export const isMpWeixin = () => {
  // #ifdef MP-WEIXIN
  return true
  // #endif
  return false
}

/**
 * 判断是否为支付宝小程序
 */
export const isMpAlipay = () => {
  // #ifdef MP-ALIPAY
  return true
  // #endif
  return false
}

/**
 * 判断是否为APP
 */
export const isApp = () => {
  // #ifdef APP-PLUS
  return true
  // #endif
  return false
}

/**
 * 环境配置工具类
 */
export class EnvConfig {
  static instance = null
  config = {}

  constructor() {
    this.init()
  }

  static getInstance() {
    if (!EnvConfig.instance) {
      EnvConfig.instance = new EnvConfig()
    }
    return EnvConfig.instance
  }

  init() {
    this.config = {
      env: getEnv(),
      isDev: isDev(),
      isStaging: isStaging(),
      isProd: isProd(),
      platform: getPlatform(),
      isH5: isH5(),
      isMpWeixin: isMpWeixin(),
      isMpAlipay: isMpAlipay(),
      isApp: isApp(),
    }
  }

  get(key) {
    return this.config[key]
  }

  getAll() {
    return { ...this.config }
  }

  is(key) {
    return this.config.env === key
  }

  has(key) {
    return key in this.config
  }
}