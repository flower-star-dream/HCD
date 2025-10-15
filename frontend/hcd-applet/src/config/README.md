# 配置使用指南

## 目录结构

```
src/config/
├── index.js          # 主配置文件，根据环境导出配置
├── types.js          # 类型定义
├── constants.js      # 常量配置
├── env.js            # 环境工具函数
└── README.md         # 使用文档
```

## 使用方法

### 1. 导入配置

```javascript
import config, { isDev, isProd, platform } from '@/config'
import { CACHE_KEY, ORDER_STATUS, SEAT_TYPE } from '@/config/constants'
```

### 2. 使用配置

```javascript
// 使用基础配置
console.log(config.baseUrl)        // API基础地址
console.log(config.ossUrl)         // OSS地址
console.log(config.timeout)        // 请求超时时间

// 使用常量
console.log(CACHE_KEY.TOKEN)       // token缓存键名
console.log(ORDER_STATUS.PENDING)  // 订单状态
console.log(SEAT_TYPE.SECOND_CLASS) // 座位类型

// 判断环境
if (isDev) {
  console.log('开发环境')
}

// 判断平台
console.log(platform)              // 当前平台
```

### 3. 环境变量配置

#### 开发环境 (process.env.NODE_ENV = 'development')
```javascript
{
  baseUrl: 'http://localhost:8080',
  ossUrl: 'https://dev-oss.example.com',
  mock: true,
  debug: true,
}
```

#### 生产环境 (process.env.NODE_ENV = 'production')
```javascript
{
  baseUrl: 'https://api.example.com',
  ossUrl: 'https://oss.example.com',
  mock: false,
  debug: false,
}
```

#### 测试环境 (process.env.NODE_ENV = 'staging')
```javascript
{
  baseUrl: 'https://staging-api.example.com',
  ossUrl: 'https://staging-oss.example.com',
  mock: false,
  debug: true,
}
```

## 配置项说明

### 基础配置 (config)
- `baseUrl`: API基础地址
- `ossUrl`: 对象存储地址
- `apiPrefix`: API前缀
- `timeout`: 请求超时时间(毫秒)
- `mock`: 是否启用mock数据
- `debug`: 是否启用调试模式
- `title`: 页面标题
- `version`: 版本号

### 常量配置 (constants)
- `CACHE_KEY`: 缓存键名
- `ORDER_STATUS`: 订单状态
- `SEAT_TYPE`: 座位类型
- `PASSENGER_TYPE`: 乘客类型
- `PAYMENT_METHOD`: 支付方式
- `HOT_CITIES`: 热门城市
- `REGEXP`: 正则表达式
- `HTTP_STATUS`: HTTP状态码

### 类型定义 (types)
- `ApiResponse`: API响应类型
- `UserInfo`: 用户信息类型
- `TicketInfo`: 车票信息类型
- `OrderInfo`: 订单信息类型
- `PassengerInfo`: 乘客信息类型

## 示例代码

### 配置请求拦截器

```javascript
// utils/request.js
import config from '@/config'
import { CACHE_KEY } from '@/config/constants'

const request = (options = {}) => {
  // 基础URL
  options.url = config.baseUrl + config.apiPrefix + options.url

  // 超时时间
  options.timeout = config.timeout

  // 添加token
  const token = uni.getStorageSync(CACHE_KEY.TOKEN)
  if (token) {
    options.header = {
      ...options.header,
      'Authorization': `Bearer ${token}`
    }
  }

  return new Promise((resolve, reject) => {
    uni.request({
      ...options,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(res)
        }
      },
      fail: reject
    })
  })
}

export default request
```

### 使用常量

```javascript
import { ORDER_STATUS, SEAT_TYPE, PAYMENT_METHOD } from '@/config/constants'

// 获取订单状态
const getOrderStatus = (status) => {
  return ORDER_STATUS[status] || { label: '未知', color: '#999' }
}

// 获取座位类型
const getSeatType = (type) => {
  return SEAT_TYPE[type] || { label: '未知', price: 0 }
}

// 获取支付方式
const getPaymentMethod = (method) => {
  return PAYMENT_METHOD[method] || { label: '未知', icon: 'question' }
}
```

### 环境判断

```javascript
import { isDev, platform, isH5, isMpWeixin } from '@/config/env'

if (isDev) {
  console.log('开发环境，启用调试模式')
}

if (isH5) {
  console.log('H5平台')
} else if (isMpWeixin) {
  console.log('微信小程序')
}

console.log('当前平台：', platform)
```

### 平台特定配置

```javascript
import config, { platform } from '@/config'

// 根据平台设置不同的配置
const getPlatformConfig = () => {
  const baseConfig = {
    ...config,
    platformSpecific: {}
  }

  // #ifdef MP-WEIXIN
  baseConfig.platformSpecific = {
    appId: 'wx123456789',
    scope: 'scope.userInfo'
  }
  // #endif

  // #ifdef H5
  baseConfig.platformSpecific = {
    wechatJsApiList: ['chooseImage', 'previewImage']
  }
  // #endif

  return baseConfig
}
```

## 扩展配置

### 添加新的环境变量

1. 在 `index.js` 中添加新的配置项：
```javascript
// 在各个环境中配置值
const configs = {
  development: {
    newKey: 'dev-value',
    // ...
  },
  production: {
    newKey: 'prod-value',
    // ...
  },
}
```

### 添加新的常量

1. 在 `constants.js` 中添加：
```javascript
export const NEW_CONSTANT = {
  KEY1: 'value1',
  KEY2: 'value2',
}
```

2. 在项目中使用：
```javascript
import { NEW_CONSTANT } from '@/config/constants'
```

### 添加新的类型

1. 在 `types.js` 中添加：
```javascript
export const NewType = {
  field1: '',
  field2: 0,
  field3: [],
}
```

## 平台判断

### 条件编译

```javascript
// #ifdef H5
console.log('H5平台特有代码')
// #endif

// #ifdef MP-WEIXIN
console.log('微信小程序特有代码')
// #endif

// #ifdef APP-PLUS
console.log('APP特有代码')
// #endif
```

### 运行时判断

```javascript
import { isH5, isMpWeixin, isApp } from '@/config/env'

if (isH5()) {
  // H5平台逻辑
} else if (isMpWeixin()) {
  // 微信小程序逻辑
} else if (isApp()) {
  // APP逻辑
}
```

## 最佳实践

1. **环境变量**: 使用 `process.env.NODE_ENV` 判断环境
2. **常量命名**: 使用全大写和下划线，如 `CACHE_KEY`
3. **类型定义**: 使用 PascalCase 命名，如 `UserInfo`
4. **条件编译**: 优先使用 Uniapp 的条件编译
5. **平台判断**: 使用工具函数，如 `isH5()`, `isMpWeixin()`

## 注意事项

1. 环境配置应该在应用启动时初始化
2. 敏感信息(如密码、密钥)不应该硬编码在配置中
3. 生产环境配置应该经过严格测试
4. 平台特定代码应该使用条件编译
5. 配置变更时应该同步更新类型定义

## 平台差异

### H5平台
- 支持完整的Web API
- 可以使用 `localStorage`
- 支持 `axios` 等HTTP库

### 小程序平台
- 使用 `uni.request` 进行网络请求
- 使用 `uni.setStorageSync` 进行数据存储
- 有包大小限制

### APP平台
- 支持原生插件
- 可以使用原生API
- 支持离线运行

## 调试建议

### 开发环境
```javascript
if (isDev) {
  // 启用调试模式
  console.log('调试信息')
  // 显示调试面板
  // 启用mock数据
}
```

### 生产环境
```javascript
if (isProd) {
  // 关闭调试模式
  // 启用错误上报
  // 启用性能监控
}
```