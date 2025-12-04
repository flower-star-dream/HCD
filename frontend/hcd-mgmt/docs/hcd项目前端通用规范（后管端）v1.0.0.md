# HCD项目前端通用规范（后管端）v1.0.0

## 1. 总则

### 1.1 规范目的
本规范旨在统一HCD火车订票系统前端后管项目的开发标准，确保代码质量、提高开发效率、降低维护成本，建立可持续的技术体系。

### 1.2 适用范围
本规范适用于HCD项目前端后管端的所有开发工作，包括代码编写、组件设计、接口对接、部署发布等各个环节。

### 1.3 规范原则
- **一致性**：保持代码风格和架构的一致性
- **可维护性**：代码结构清晰，易于理解和维护
- **可扩展性**：设计灵活，便于功能扩展和修改
- **性能优先**：在保证功能的前提下优化性能
- **安全第一**：严格遵守安全开发规范

## 2. 技术栈规范

### 2.1 核心技术栈
```
前端框架：Vue 3.3+
开发语言：TypeScript 5.0+
UI框架：Element Plus 2.4+
状态管理：Pinia 2.1+
路由管理：Vue Router 4.2+
HTTP客户端：Axios 1.6+
构建工具：Vite 5.0+
样式预处理：SCSS/Sass
代码检查：ESLint + Prettier
```

### 2.2 浏览器兼容性
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+
- 移动端：iOS Safari 14+, Chrome for Android 90+

### 2.3 Node.js环境
- 开发环境：Node.js 18.0+
- 构建环境：Node.js 18.0+
- 包管理器：npm 9.0+ 或 pnpm 8.0+

## 3. 项目结构规范

### 3.1 目录结构
```
src/
├── api/                    # API接口层
│   ├── modules/           # 业务模块API
│   └── index.ts           # API统一导出
├── assets/                # 静态资源
│   ├── images/           # 图片资源
│   ├── icons/            # 图标资源
│   └── styles/           # 全局样式
├── components/            # 公共组件
│   ├── common/           # 通用组件
│   ├── business/         # 业务组件
│   └── index.ts          # 组件统一导出
├── config/               # 配置文件
│   ├── index.ts          # 基础配置
│   ├── api.ts            # API配置
│   └── theme.ts          # 主题配置
├── directives/           # 自定义指令
├── hooks/                # 组合式函数
├── layout/               # 布局组件
├── router/               # 路由配置
│   ├── modules/          # 路由模块
│   └── index.ts          # 路由主文件
├── stores/               # 状态管理
│   ├── modules/          # 状态模块
│   └── index.ts          # 状态主文件
├── styles/               # 样式文件
│   ├── variables.scss    # 样式变量
│   ├── mixins.scss       # 样式混入
│   └── global.scss       # 全局样式
├── types/                # 类型定义
│   ├── api.ts            # API类型
│   ├── component.ts      # 组件类型
│   └── global.d.ts       # 全局类型
├── utils/                # 工具函数
│   ├── request.ts        # 请求工具
│   ├── storage.ts        # 存储工具
│   └── index.ts          # 工具统一导出
└── views/                # 页面组件
    ├── login/            # 登录页面
    ├── dashboard/        # 控制台
    ├── user/             # 用户管理
    ├── order/            # 订单管理
    └── ...               # 其他业务模块
```

### 3.2 文件命名规范
- **组件文件**：使用 PascalCase（如：`UserList.vue`）
- **工具函数**：使用 camelCase（如：`formatDate.ts`）
- **常量定义**：使用 UPPER_SNAKE_CASE（如：`API_BASE_URL`）
- **样式文件**：使用 kebab-case（如：`user-list.scss`）

## 4. 编码规范

### 4.1 Vue组件规范

#### 4.1.1 组件结构
```vue
<template>
  <!-- 模板内容 -->
</template>

<script setup lang="ts">
// TypeScript 类型定义
type Props = {
  title: string
  visible: boolean
}

// Props 定义
const props = withDefaults(defineProps<Props>(), {
  title: '',
  visible: false
})

// Emits 定义
const emit = defineEmits<{
  update: [value: string]
  close: []
}>()

// 响应式数据
const loading = ref(false)
const list = ref<Array<any>>([])

// 计算属性
const computedData = computed(() => {
  return list.value.filter(item => item.status === 1)
})

// 方法定义
const handleClick = () => {
  // 方法实现
}

// 生命周期
onMounted(() => {
  // 初始化逻辑
})
</script>

<style scoped lang="scss">
/* 样式内容 */
</style>
```

#### 4.1.2 Props规范
- 使用 TypeScript 接口定义 Props 类型
- 提供默认值时使用 `withDefaults`
- Props 命名使用 camelCase
- 必填字段使用 `required: true`

#### 4.1.3 Emits规范
- 使用 TypeScript 泛型定义事件类型
- 事件命名使用 camelCase
- 传递参数时明确定义参数类型

### 4.2 TypeScript规范

#### 4.2.1 类型定义
```typescript
// 接口命名使用 PascalCase
interface UserInfo {
  id: number
  name: string
  email?: string // 可选属性
}

// 类型别名使用 PascalCase
type UserStatus = 'active' | 'inactive' | 'pending'

// 枚举使用 PascalCase
enum OrderStatus {
  Pending = 0,
  Paid = 1,
  Completed = 2
}
```

#### 4.2.2 函数定义
```typescript
// 函数参数和返回值必须定义类型
function getUserList(params: UserParams): Promise<UserInfo[]> {
  // 函数实现
}

// 箭头函数类型定义
const formatDate = (date: Date): string => {
  // 函数实现
}
```

### 4.3 样式规范

#### 4.3.1 SCSS规范
```scss
// 变量命名使用 kebab-case
$primary-color: #409eff;
$border-radius: 4px;

// 嵌套层级不超过3层
.user-list {
  .list-item {
    padding: 10px;

    &:hover {
      background-color: #f5f5f5;
    }
  }
}

// 使用BEM命名规范
.user-card {
  &__header {
    font-size: 16px;
  }

  &__content {
    padding: 20px;
  }

  &--active {
    border-color: $primary-color;
  }
}
```

#### 4.3.2 样式作用域
- 组件样式统一使用 `scoped`
- 全局样式放在 `src/styles/global.scss`
- 避免使用全局选择器污染全局样式

## 5. API接口规范

### 5.1 接口设计原则
- **RESTful风格**：使用标准的HTTP方法和状态码
- **统一响应格式**：所有接口返回统一的数据结构
- **版本控制**：API版本号管理
- **错误处理**：统一的错误码和错误信息

### 5.2 请求规范
```typescript
// 请求参数类型定义
interface LoginParams {
  username: string
  password: string
}

// 响应数据类型定义
interface LoginResponse {
  token: string
  userInfo: UserInfo
}

// API函数定义
export const login = (params: LoginParams): Promise<LoginResponse> => {
  return request.post('/api/auth/login', params)
}
```

### 5.3 响应格式规范
```typescript
// 统一响应格式
interface ApiResponse<T> {
  code: number      // 状态码
  message: string   // 响应消息
  data: T          // 响应数据
  success: boolean // 是否成功
}
```

### 5.4 错误处理规范
```typescript
// 错误码定义
enum ErrorCode {
  SUCCESS = 200,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  SERVER_ERROR = 500
}

// 错误处理
const handleError = (error: any) => {
  const { code, message } = error

  switch (code) {
    case ErrorCode.UNAUTHORIZED:
      // 未授权处理
      router.push('/login')
      break
    case ErrorCode.FORBIDDEN:
      // 无权限处理
      ElMessage.error('没有操作权限')
      break
    default:
      ElMessage.error(message || '系统错误')
  }
}
```

## 6. 状态管理规范

### 6.1 Pinia Store规范
```typescript
// store定义
import { defineStore } from 'pinia'

interface UserState {
  token: string
  userInfo: UserInfo | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: '',
    userInfo: null
  }),

  getters: {
    isLogin: (state): boolean => !!state.token,
    userName: (state): string => state.userInfo?.name || ''
  },

  actions: {
    setToken(token: string) {
      this.token = token
    },

    async login(params: LoginParams) {
      const res = await login(params)
      this.token = res.token
      this.userInfo = res.userInfo
      return res
    },

    logout() {
      this.token = ''
      this.userInfo = null
      router.push('/login')
    }
  }
})
```

### 6.2 状态管理原则
- **模块化**：按业务模块划分store
- **类型安全**：使用TypeScript定义状态类型
- **持久化**：重要状态支持持久化存储
- **响应式**：充分利用Vue的响应式系统

## 7. 路由规范

### 7.1 路由配置规范
```typescript
// 路由类型定义
interface RouteMeta {
  title: string          // 页面标题
  requiresAuth: boolean  // 是否需要登录
  roles?: string[]      // 访问角色权限
  keepAlive?: boolean   // 是否缓存
}

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/user/list',
    name: 'UserList',
    component: () => import('@/views/user/UserList.vue'),
    meta: {
      title: '用户列表',
      requiresAuth: true,
      roles: ['admin', 'super-admin'],
      keepAlive: true
    }
  }
]
```

### 7.2 路由守卫规范
```typescript
// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 登录验证
  if (to.meta.requiresAuth && !userStore.isLogin) {
    next('/login')
    return
  }

  // 权限验证
  if (to.meta.roles && !userStore.hasRole(to.meta.roles)) {
    next('/403')
    return
  }

  next()
})
```

## 8. 组件设计规范

### 8.1 组件设计原则
- **单一职责**：每个组件只负责一个功能
- **高内聚**：组件内部功能紧密相关
- **低耦合**：组件之间依赖关系最小化
- **可复用**：设计通用的组件接口
- **可测试**：组件易于单元测试

### 8.2 组件通信规范
```typescript
// Props传递（父传子）
const props = defineProps<{
  data: any[]
  loading: boolean
}>()

// 事件传递（子传父）
const emit = defineEmits<{
  select: [item: any]
  update: [value: string]
}>()

// 全局状态（跨组件通信）
const userStore = useUserStore()
```

### 8.3 组件文档规范
```vue
<!--
* @name: UserCard
* @description: 用户卡片组件，用于展示用户基本信息
* @props:
*   - user: UserInfo 用户数据
*   - editable: boolean 是否可编辑，默认false
* @emits:
*   - edit: 点击编辑按钮时触发
*   - delete: 点击删除按钮时触发
* @example:
*   <UserCard :user="userInfo" :editable="true" @edit="handleEdit" />
-->
```

## 9. 性能优化规范

### 9.1 代码分割
```typescript
// 路由懒加载
const UserList = () => import('@/views/user/UserList.vue')

// 组件异步加载
const AsyncComponent = defineAsyncComponent(() =>
  import('@/components/ComplexComponent.vue')
)
```

### 9.2 资源优化
- **图片优化**：使用适当的图片格式和大小
- **图标优化**：使用SVG图标，支持按需加载
- **字体优化**：使用字体子集化技术
- **CDN加速**：静态资源使用CDN分发

### 9.3 渲染优化
```typescript
// 使用v-show代替v-if（频繁切换时）
<div v-show="isVisible">内容</div>

// 使用key优化列表渲染
<div v-for="item in list" :key="item.id">{{ item.name }}</div>

// 使用computed缓存计算结果
const filteredList = computed(() =>
  list.value.filter(item => item.status === 1)
)
```

## 10. 安全规范

### 10.1 输入验证
```typescript
// 表单验证
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{3,20}$/, message: '用户名格式错误', trigger: 'blur' }
  ]
}

// XSS防护
const sanitizeHtml = (html: string): string => {
  return html.replace(/<script[^>]*>.*?<\/script>/gi, '')
}
```

### 10.2 权限控制
```typescript
// 按钮级别权限控制
const hasPermission = (permission: string): boolean => {
  const userStore = useUserStore()
  return userStore.permissions.includes(permission)
}

// 路由权限控制
const routes: RouteRecordRaw[] = [
  {
    path: '/admin',
    component: AdminView,
    meta: {
      roles: ['admin', 'super-admin']
    }
  }
]
```

### 10.3 数据安全
- **敏感信息加密**：密码、手机号等敏感数据加密存储
- **HTTPS通信**：所有API请求使用HTTPS协议
- **Token安全**：JWT Token安全存储和传输
- **错误信息**：不暴露系统内部错误信息

## 11. 测试规范

### 11.1 单元测试
```typescript
// 组件单元测试
import { mount } from '@vue/test-utils'
import UserCard from '@/components/UserCard.vue'

describe('UserCard', () => {
  it('renders user info correctly', () => {
    const user = { name: 'John', email: 'john@example.com' }
    const wrapper = mount(UserCard, {
      props: { user }
    })

    expect(wrapper.text()).toContain(user.name)
    expect(wrapper.text()).toContain(user.email)
  })
})
```

### 11.2 集成测试
- **API测试**：测试接口调用和数据处理
- **路由测试**：测试路由跳转和权限控制
- **状态管理测试**：测试状态变更和响应式更新

### 11.3 测试覆盖率
- **单元测试覆盖率**：≥ 80%
- **集成测试覆盖率**：≥ 70%
- **关键业务逻辑**：100% 覆盖

## 12. 部署规范

### 12.1 构建配置
```typescript
// vite.config.ts
export default defineConfig({
  build: {
    target: 'es2015',
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'],
          element: ['element-plus']
        }
      }
    }
  }
})
```

### 12.2 环境配置
```typescript
// 环境变量配置
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_VERSION: string
}

// 环境配置
const config = {
  development: {
    apiBaseUrl: 'http://localhost:8080',
    mockEnabled: true
  },
  production: {
    apiBaseUrl: 'https://api.hcd.com',
    mockEnabled: false
  }
}
```

### 12.3 容器化部署
```dockerfile
# Dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 13. 文档规范

### 13.1 代码注释
```typescript
/**
 * 用户登录
 * @param params 登录参数
 * @returns 登录结果
 * @throws 登录失败时抛出错误
 */
async function login(params: LoginParams): Promise<LoginResponse> {
  // 实现代码
}
```

### 13.2 README文档
- **项目简介**：项目背景和目标
- **技术栈**：使用的技术框架和版本
- **快速开始**：环境搭建和运行步骤
- **项目结构**：目录结构说明
- **开发规范**：编码规范和最佳实践
- **部署指南**：部署步骤和注意事项

### 13.3 API文档
- **接口说明**：接口功能和业务逻辑
- **请求参数**：参数类型、是否必填、默认值
- **响应格式**：响应数据结构
- **错误码**：错误码定义和处理方式
- **示例代码**：请求和响应示例

## 14. 版本管理规范

### 14.1 版本号规范
采用语义化版本号（Semantic Versioning）：
- **主版本号（MAJOR）**：不兼容的API修改
- **次版本号（MINOR）**：向下兼容的功能性新增
- **修订号（PATCH）**：向下兼容的问题修正

格式：`主版本号.次版本号.修订号`（如：1.2.3）

### 14.2 分支管理
```
main/master     # 主分支，生产环境代码
develop         # 开发分支，集成开发代码
feature/xxx     # 功能分支，新功能开发
bugfix/xxx      # 修复分支，问题修复
hotfix/xxx      # 热修复分支，紧急修复
release/xxx     # 发布分支，版本发布
```

### 14.3 提交规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

**type类型**：
- feat：新功能
- fix：修复
- docs：文档
- style：格式
- refactor：重构
- test：测试
- chore：构建/工具

## 15. 质量保障

### 15.1 代码审查
- **审查内容**：代码逻辑、性能、安全、规范
- **审查流程**：提交PR → 代码审查 → 修改完善 → 合并代码
- **审查标准**：必须符合本规范要求

### 15.2 自动化检查
- **代码格式**：Prettier自动格式化
- **代码质量**：ESLint代码检查
- **类型检查**：TypeScript类型检查
- **测试执行**：自动化测试执行

### 15.3 性能监控
- **加载时间**：页面加载时间监控
- **运行性能**：运行时性能指标
- **错误监控**：JavaScript错误收集
- **用户行为**：用户操作路径分析

---

**规范版本**：v1.0.0
**制定日期**：2024年12月
**制定人员**：HCD项目技术团队
**审核人员**：技术架构师、前端负责人
**生效日期**：发布之日起生效

**修订记录**：
- v1.0.0 (2024-12) - 初始版本，建立完整的规范体系

---

**附录**：
- 相关工具配置文档
- 最佳实践示例代码
- 常见问题解决方案
- 规范执行检查清单