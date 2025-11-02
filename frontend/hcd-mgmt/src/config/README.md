# 配置管理说明

## 概述

本项目采用了集中式的配置管理方案，将所有环境的配置集中在 `src/config/index.ts` 文件中，使配置修改更加简单高效，无需修改多个 `.env` 文件。

## 配置管理方式

所有配置都集中在 `src/config/index.ts` 文件中，采用了以下结构：

1. **默认配置** - 定义所有环境共享的基础配置
2. **环境特定配置** - 定义各环境特有的配置，用于覆盖默认配置

## 如何修改配置

现在你只需要修改 `src/config/index.ts` 文件即可，无需修改任何 `.env` 文件。

### 示例：修改所有环境的 API 前缀

1. 打开 `src/config/index.ts` 文件
2. 修改 `defaultConfig` 对象中的 `apiPrefix` 属性：
   ```typescript
   const defaultConfig: Config = {
     // ... 其他配置 ...
     apiPrefix: '/api/v2', // 修改为新的前缀
     // ... 其他配置 ...
   }
   ```

### 示例：修改测试环境的超时时间

1. 打开 `src/config/index.ts` 文件
2. 修改 `envConfigs.staging` 对象中的 `timeout` 属性：
   ```typescript
   const envConfigs: Record<string, Partial<Config>> = {
     // ... 其他环境配置 ...
     staging: {
       // ... 其他配置 ...
       timeout: 15000, // 修改为新的超时时间
       // ... 其他配置 ...
     },
     // ... 其他环境配置 ...
   }
   ```

## 可用的配置项

### API 配置
- `baseUrl` - API 基础地址
- `apiPrefix` - API 路径前缀
- `timeout` - 请求超时时间（毫秒）

### OSS 配置
- `ossUrl` - 文件存储服务地址

### 功能开关
- `mock` - 是否启用 Mock 数据（true/false）
- `debug` - 是否启用调试模式（true/false）

### 其他配置
- `title` - 应用标题

## 添加新的配置项

如果需要添加新的配置项，请按照以下步骤操作：

1. 在 `src/config/types.ts` 的 `Config` 接口中添加对应的类型定义
2. 在 `src/config/index.ts` 的 `defaultConfig` 对象中添加默认值
3. 在需要覆盖的环境中，在 `envConfigs` 对应的环境对象中添加特定值

## 环境判断

系统提供了三个辅助函数来判断当前环境：

- `isDev` - 是否为开发环境
- `isStaging` - 是否为测试环境
- `isProd` - 是否为生产环境

## 构建和运行时环境

- 开发环境：`npm run dev`
- 测试环境：`npm run build:staging`
- 生产环境：`npm run build:prod`

系统会根据构建命令自动选择对应的环境配置。