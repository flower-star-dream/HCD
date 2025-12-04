# HCD项目部署指南

本文档详细描述了HCD项目的CI/CD配置和部署流程，包括Docker Compose配置、GitHub Actions工作流以及部署脚本的使用。

## 🏗️ 项目架构

HCD项目采用微服务架构，包含以下主要组件：

### 后端服务
- **网关服务 (hcd-gateway)**: 8080端口，统一入口和路由
- **用户服务 (user-service)**: 8081端口，用户管理相关功能
- **座位服务 (trainseat-service)**: 8082端口，座位管理
- **票务服务 (ticket-service)**: 8083端口，票务管理
- **订单服务 (order-service)**: 8084端口，订单处理
- **系统服务 (system-service)**: 8085端口，系统管理

### 前端应用
- **管理界面 (hcd-mgmt)**: Vue.js + Element Plus

### 中间件服务
- **Nacos**: 服务发现和配置管理 (8848端口)
- **MySQL**: 数据存储 (3306端口)
- **Redis**: 缓存服务 (6379端口)
- **Seata**: 分布式事务管理 (8091端口)
- **Nginx**: 反向代理和负载均衡 (80/443端口)

## 🚀 快速开始

### 1. 环境要求

- Docker Engine 20.10+
- Docker Compose 2.0+
- Git 2.20+
- 8GB+ 可用内存
- 50GB+ 可用磁盘空间

### 2. 本地开发部署

```bash
# 克隆项目
git clone https://github.com/flower-star-dream/HCD.git
cd HCD

# 使用部署脚本启动开发环境
./deploy.sh -e dev

# 或者手动启动
docker-compose up -d
```

### 3. 访问服务

- 管理界面: http://localhost/mgmt/
- API网关: http://localhost:8080/
- Nacos控制台: http://localhost:8848/nacos
- 文档服务: http://localhost:8080/doc.html

## 🔄 CI/CD流程

### GitHub Actions工作流

项目配置了完整的CI/CD流水线，包含以下工作流：

#### 1. 主CI/CD工作流 (`.github/workflows/ci-cd.yml`)

**触发条件:**
- 推送到 `main`, `develop`, `feature/release-*` 分支
- 创建Pull Request
- 手动触发

**执行步骤:**
1. **代码质量检查**: 单元测试、代码风格检查、SonarCloud扫描
2. **构建后端服务**: 多服务并行构建Docker镜像
3. **构建前端应用**: 构建Vue.js应用并打包
4. **构建Nginx**: 构建反向代理服务
5. **安全扫描**: 容器镜像漏洞扫描
6. **部署测试环境**: 自动部署到测试服务器
7. **部署生产环境**: 生产环境滚动部署
8. **回滚功能**: 支持快速回滚到上一个版本

#### 2. 安全扫描工作流 (`.github/workflows/security-scan.yml`)

**功能:**
- 依赖漏洞扫描 (OWASP Dependency Check, npm audit)
- 容器镜像安全扫描 (Trivy, Snyk)
- 代码安全分析 (CodeQL, Semgrep)
- 密钥泄露检测 (GitLeaks, TruffleHog)
- 基础设施扫描 (Checkov, tfsec)

**执行频率:**
- 每周定期执行
- 代码变更时触发
- 手动触发

### 环境配置

#### 开发环境 (`docker-compose.yml`)
- 单实例部署
- 使用本地构建的镜像
- 开发配置和调试信息
- 适合本地开发和测试

#### 测试环境 (`docker-compose.test.yml`)
- 使用GitHub Container Registry镜像
- 测试配置和参数
- 自动CI/CD部署
- 适合集成测试

#### 生产环境 (`docker-compose.prod.yml`)
- 高可用配置 (多副本)
- 资源限制和约束
- SSL/TLS支持
- 生产级监控和日志
- 滚动更新和回滚

## 📋 部署脚本使用

### 部署脚本功能

`deploy.sh` 提供了完整的部署管理功能：

```bash
# 查看帮助信息
./deploy.sh --help

# 开发环境部署
./deploy.sh -e dev

# 测试环境部署并备份
./deploy.sh -e test -b

# 生产环境部署
./deploy.sh -e prod -b

# 查看服务状态
./deploy.sh -s

# 查看日志
./deploy.sh -l [service-name]

# 回滚到上一个版本
./deploy.sh -r

# 清理无用资源
./deploy.sh -c
```

### 部署参数说明

| 参数 | 说明 | 示例 |
|------|------|------|
| `-e, --environment` | 部署环境 (dev/test/prod) | `./deploy.sh -e prod` |
| `-b, --backup` | 部署前创建备份 | `./deploy.sh -e prod -b` |
| `-r, --rollback` | 回滚到上一个版本 | `./deploy.sh -r` |
| `-c, --cleanup` | 清理无用资源 | `./deploy.sh -c` |
| `-s, --status` | 查看服务状态 | `./deploy.sh -s` |
| `-l, --logs` | 查看日志 | `./deploy.sh -l nginx` |
| `--build` | 重新构建镜像 | `./deploy.sh --build` |
| `--pull` | 拉取最新镜像 | `./deploy.sh --pull` |

## 🔧 配置说明

### 后端服务配置

每个后端服务都支持通过环境变量进行配置：

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod        # 激活的配置文件
  - NACOS_SERVER_ADDR=nacos:8848       # Nacos服务器地址
  - NACOS_NAMESPACE=8a19b7dd-7c6e-4a98-8bcf-b81ebd4aad2c  # 命名空间
```

### 中间件配置

#### Nacos配置
- 服务端配置: `backend/conf/server/nacos/`
- 客户端配置: `backend/conf/client/nacos/`
- 命名空间管理: 每个文件夹名对应一个命名空间

#### MySQL配置
- 配置文件: `backend/conf/server/mysql/mysql.cnf`
- 支持字符集、连接池、日志等配置

#### Redis配置
- 配置文件: `backend/conf/server/redis/redis.conf`
- 支持持久化、内存管理、安全配置

#### Seata配置
- 配置文件: `backend/conf/server/seata/registry.conf`
- 支持分布式事务管理

### Nginx配置

主配置文件: `frontend/nginx/conf/default.conf`

包含以下代理配置：
- `/mgmt/` -> 前端管理界面
- `/api/` -> 后端API网关
- 各微服务路径代理
- 健康检查端点

## 🔒 安全配置

### 容器安全
- 使用非root用户运行容器
- 最小化基础镜像
- 定期更新基础镜像
- 容器运行时安全扫描

### 网络安全
- 服务间网络隔离
- 防火墙配置
- SSL/TLS加密传输
- API访问控制

### 数据安全
- 数据库连接加密
- 敏感信息环境变量注入
- 定期备份策略
- 数据脱敏处理

## 📊 监控和日志

### 健康检查
所有服务都配置了健康检查端点：
- HTTP: `http://localhost:8080/actuator/health`
- 整体: `http://localhost/health`

### 日志管理
- 应用日志: `backend/logs/`
- Nginx日志: `frontend/nginx/logs/`
- 容器日志: `docker-compose logs [service-name]`

### 性能监控
- 资源使用率监控
- 服务响应时间监控
- 错误率监控
- 业务指标监控

## 🚨 故障排查

### 常见问题

1. **服务启动失败**
   ```bash
   # 查看服务日志
   ./deploy.sh -l [service-name]

   # 检查服务状态
   ./deploy.sh -s
   ```

2. **数据库连接问题**
   - 检查MySQL服务状态
   - 验证数据库配置
   - 检查网络连通性

3. **Nacos注册失败**
   - 检查Nacos服务状态
   - 验证命名空间配置
   - 检查网络配置

4. **内存不足**
   - 调整Docker资源限制
   - 优化JVM参数
   - 增加物理内存

### 日志分析

```bash
# 查看所有服务日志
./deploy.sh -l

# 查看特定服务日志
./deploy.sh -l nginx

# 实时跟踪日志
docker-compose logs -f [service-name]
```

## 🔧 维护和更新

### 定期维护任务

1. **日志清理**
   ```bash
   # 清理旧日志
   find backend/logs -name "*.log" -mtime +30 -delete
   ```

2. **镜像更新**
   ```bash
   # 拉取最新基础镜像
   ./deploy.sh --pull
   ```

3. **备份验证**
   ```bash
   # 创建备份
   ./deploy.sh -e prod -b
   ```

4. **安全扫描**
   - 定期运行安全扫描工作流
   - 及时修复发现的漏洞
   - 更新安全策略

### 版本更新流程

1. **开发环境测试**
   ```bash
   ./deploy.sh -e dev --build
   ```

2. **测试环境验证**
   ```bash
   ./deploy.sh -e test -b
   ```

3. **生产环境部署**
   ```bash
   ./deploy.sh -e prod -b
   ```

4. **回滚准备**
   ```bash
   # 如需要回滚
   ./deploy.sh -r
   ```

## 📞 支持和联系

### 技术支持
- GitHub Issues: https://github.com/flower-star-dream/HCD/issues
- 文档更新: 提交PR或Issue

### 贡献指南
1. Fork项目仓库
2. 创建功能分支
3. 提交代码变更
4. 创建Pull Request
5. 通过CI/CD检查

---

**注意**: 本部署指南会随项目更新而更新，请定期查看最新版本。