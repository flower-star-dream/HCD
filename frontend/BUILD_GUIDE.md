# 构建指南 - 火车订票系统前端项目

## 项目概述

本项目包含两个前端应用：
- **hcd-mgmt**: 后台管理系统 (Vue3 + Element Plus)
- **hcd-applet**: 用户小程序 (Uniapp + uView Plus)

## 技术栈

### hcd-mgmt (后台管理系统)
- **框架**: Vue 3 + TypeScript
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **构建工具**: Vite + Webpack 5 (双构建系统)
- **HTTP客户端**: Axios

### hcd-applet (用户小程序)
- **框架**: Uniapp (Vue 3)
- **UI库**: uView Plus
- **构建工具**: Uniapp CLI + Webpack 5 (H5专用)
- **支持平台**: 微信小程序、支付宝小程序、H5、APP

## 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0
- 推荐使用 pnpm 作为包管理器

## 快速开始

### 安装依赖

```bash
# 安装hcd-mgmt依赖
cd frontend/hcd-mgmt
npm install

# 安装hcd-applet依赖
cd frontend/hcd-applet
npm install
```

## 开发模式

### hcd-mgmt (后台管理系统)

#### 使用Vite (推荐开发模式)
```bash
npm run dev          # 启动Vite开发服务器 (端口: 5173)
```

#### 使用Webpack
```bash
npm run dev:webpack  # 启动Webpack开发服务器 (端口: 3000)
```

#### 其他开发命令
```bash
npm run preview      # 预览构建结果
npm run lint         # ESLint代码检查
npm run format       # Prettier代码格式化
```

### hcd-applet (小程序)

#### 使用Uniapp CLI
```bash
# 开发模式
npm run dev:h5              # H5开发 (端口: 3000)
npm run dev:mp-weixin       # 微信小程序开发
npm run dev:mp-alipay       # 支付宝小程序开发

# 构建模式
npm run build:h5            # H5构建
npm run build:mp-weixin     # 微信小程序构建
npm run build:mp-alipay     # 支付宝小程序构建
```

#### 使用Webpack (H5专用)
```bash
npm run dev:h5:webpack      # Webpack开发服务器 (端口: 3001)
npm run build:h5:webpack    # Webpack生产构建
```

## 生产构建

### hcd-mgmt

#### 标准构建
```bash
# 使用Vite构建
npm run build

# 使用Webpack构建
npm run build:webpack

# 构建并分析包大小
npm run build:analyze

# 同时用两种构建工具构建
npm run build:all
```

#### 环境特定构建
```bash
npm run build:staging      # 预发布环境构建
npm run serve             # 本地服务器部署测试
```

### hcd-applet

#### 标准构建
```bash
# H5构建
npm run build:h5
npm run build:h5:webpack

# 小程序构建
npm run build:mp-weixin
npm run build:mp-alipay

# 包分析
npm run build:h5:webpack:analyze
```

## 构建输出

### hcd-mgmt
- **Vite构建**: `dist/` 目录
- **Webpack构建**: `dist/` 目录
  - `js/` - JavaScript文件 (带hash)
  - `css/` - CSS文件 (带hash)
  - `images/` - 图片资源
  - `fonts/` - 字体文件

### hcd-applet
- **Uniapp构建**: `dist/build/` 目录
- **Webpack构建**: `dist/build/h5/` 目录

## 性能优化

### Webpack优化特性
- **代码分割**: 自动分割第三方库和公共代码
- **缓存**: 长期缓存策略 (contenthash)
- **压缩**: Terser压缩JavaScript
- **分析**: Bundle Analyzer集成

### hcd-mgmt优化配置
- **vendor分割**: 第三方库单独打包
- **Element Plus分割**: UI库单独打包
- **公共代码提取**: 2次以上引用的代码提取
- **运行时优化**: 运行时单独打包

### hcd-applet优化配置
- **uView Plus分割**: UI库单独打包
- **多平台优化**: 针对H5的特定优化

## 部署配置

### 环境变量

#### hcd-mgmt
```bash
# 开发环境
NODE_ENV=development

# 生产环境
NODE_ENV=production

# 分析模式
ANALYZE=true
```

#### hcd-applet
```bash
# Webpack分析模式
ANALYZE=true

# 预发布环境
NODE_ENV=staging
```

### Docker部署

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down
```

### Nginx配置

项目已包含完整的Nginx配置：
- **hcd-mgmt**: http://localhost:8080
- **hcd-applet**: http://localhost:8081
- **API代理**: 自动代理到后端服务

## 构建脚本详解

### hcd-mgmt

| 脚本 | 描述 | 端口 |
|------|------|------|
| `dev` | Vite开发服务器 | 5173 |
| `dev:webpack` | Webpack开发服务器 | 3000 |
| `build` | Vite生产构建 | - |
| `build:webpack` | Webpack生产构建 | - |
| `build:analyze` | 构建并分析包大小 | - |
| `build:staging` | 预发布环境构建 | - |
| `build:all` | 双构建工具构建 | - |

### hcd-applet

| 脚本 | 描述 | 平台 |
|------|------|------|
| `dev:h5` | Uniapp H5开发 | H5 |
| `dev:mp-weixin` | 微信小程序开发 | 微信小程序 |
| `build:h5` | Uniapp H5构建 | H5 |
| `build:h5:webpack` | Webpack H5构建 | H5 |
| `build:mp-weixin` | 微信小程序构建 | 微信小程序 |

## 故障排查

### 常见问题

1. **端口占用**
   ```bash
   # 检查端口占用
   netstat -ano | findstr :3000
   ```

2. **依赖安装失败**
   ```bash
   # 清理缓存
   npm cache clean --force

   # 删除node_modules
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **构建失败**
   ```bash
   # 检查TypeScript
   npm run type-check

   # 检查ESLint
   npm run lint
   ```

### 性能监控

#### Bundle分析
```bash
# hcd-mgmt
npm run build:analyze

# hcd-applet
npm run build:h5:webpack:analyze
```

#### 构建时间分析
```bash
# 查看详细构建信息
npm run build:webpack -- --stats verbose
```

## CI/CD集成

项目已配置Jenkins Pipeline，支持：
- 自动构建
- 代码质量检查
- 单元测试
- 部署到测试环境
- 部署到生产环境

详细配置请参考 `.jenkins/Jenkinsfile`

## 联系支持

如有问题，请联系开发团队或提交Issue。