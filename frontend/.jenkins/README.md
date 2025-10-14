# Jenkins CI/CD 配置文档

## 概述
本文档描述了火车订票系统前端项目的Jenkins持续集成和持续部署配置。

## 项目结构
```
.jenkins/
├── Jenkinsfile          # Jenkins Pipeline 配置文件
├── deploy.sh            # 部署脚本
├── README.md            # 本文档
└── docker-compose.yml   # Docker Compose 配置
```

## 环境要求

### Jenkins 插件
- Pipeline
- Docker Pipeline
- NodeJS Plugin
- Git Plugin
- SSH Agent Plugin

### 系统依赖
- Docker
- Docker Compose
- Node.js 18+
- npm

## 配置步骤

### 1. Jenkins 全局配置

#### NodeJS 配置
1. 进入 Jenkins 管理 → 全局工具配置
2. 找到 NodeJS 部分，添加 NodeJS 18 安装
3. 勾选 "自动安装" 并选择 "18.x.x" 版本

#### Docker 配置
1. 确保 Jenkins 用户有权限运行 Docker
2. 在 Jenkins 管理 → 系统配置中配置 Docker 连接

### 2. 凭据配置

在 Jenkins 凭据管理中添加以下凭据：

#### SSH 凭据
- ID: `deploy-ssh-key`
- 类型: SSH Username with private key
- 用户名: `deploy`
- 私钥: 部署服务器的 SSH 私钥

#### Docker Registry 凭据（如需要）
- ID: `docker-registry-creds`
- 类型: Username with password
- 用户名: Docker registry 用户名
- 密码: Docker registry 密码

### 3. 环境变量配置

在 Jenkins 中配置以下环境变量：

```bash
DOCKER_REGISTRY=your-registry.com
DEPLOY_HOST=your-server.com
DEPLOY_USER=deploy
```

## 构建流程

### Pipeline 阶段

1. **Checkout**: 拉取代码
2. **Install Dependencies**: 安装项目依赖
3. **Lint**: 代码质量检查
4. **Build**: 构建项目
5. **Test**: 运行测试
6. **Build Docker Images**: 构建 Docker 镜像
7. **Push to Registry**: 推送镜像到仓库
8. **Deploy**: 部署到服务器

### 分支策略
- `main` 分支: 生产环境部署
- `develop` 分支: 测试环境部署
- 其他分支: 仅构建和测试

## 部署配置

### 服务器配置
在部署服务器上创建以下目录结构：
```
/opt/
├── hcd-staging/          # 测试环境
│   ├── docker-compose.yml
│   └── .env
└── hcd-prod/            # 生产环境
    ├── docker-compose.yml
    └── .env
```

### 环境文件
复制相应的环境配置文件：
```bash
# 测试环境
cp .env.staging /opt/hcd-staging/.env

# 生产环境
cp .env.production /opt/hcd-prod/.env
```

## 使用说明

### 手动触发构建
1. 在 Jenkins 中打开项目
2. 点击 "Build Now"
3. 选择分支（如需要）
4. 点击 "Build"

### 自动触发构建
配置 Git Webhook 自动触发构建：
1. 在代码仓库中设置 Webhook
2. URL: `http://jenkins-server/github-webhook/`
3. 事件: Push events

## 故障排除

### 常见问题

#### 1. 构建失败 - 依赖安装
```bash
# 清除缓存
npm cache clean --force
# 重新安装
rm -rf node_modules package-lock.json
npm install
```

#### 2. Docker 构建失败
```bash
# 检查 Docker 服务
sudo systemctl status docker
# 检查权限
sudo usermod -aG docker jenkins
```

#### 3. 部署失败 - SSH 连接
```bash
# 测试 SSH 连接
ssh -i /path/to/key deploy@your-server.com
# 检查防火墙
sudo ufw status
```

### 日志查看
- Jenkins 控制台日志
- 服务器上的 Docker 日志:
  ```bash
  docker logs -f hcd-nginx
  ```
- Nginx 日志:
  ```bash
  tail -f /var/log/nginx/access.log
  tail -f /var/log/nginx/error.log
  ```

## 性能优化

### 构建优化
1. 使用 npm ci 替代 npm install
2. 启用并行构建
3. 缓存依赖
4. 使用多阶段 Docker 构建

### 部署优化
1. 使用蓝绿部署
2. 健康检查
3. 自动回滚
4. 零停机部署

## 安全考虑

1. 使用 SSH 密钥认证
2. 定期更新依赖
3. 扫描 Docker 镜像漏洞
4. 使用 HTTPS
5. 配置适当的 CORS 策略

## 监控和告警

### 监控指标
- 构建成功率
- 构建时间
- 部署成功率
- 部署时间

### 告警配置
- 构建失败告警
- 部署失败告警
- 健康检查失败告警

## 回滚策略

### 自动回滚
当健康检查失败时，自动回滚到上一个版本。

### 手动回滚
```bash
# 使用部署脚本回滚
./deploy.sh rollback
```

## 最佳实践

1. **代码审查**: 所有代码变更都需要审查
2. **自动化测试**: 保持高测试覆盖率
3. **小步快跑**: 频繁的小变更比大变更更安全
4. **监控**: 实时监控应用状态
5. **文档**: 保持文档更新

## 更新日志

### v1.0.0
- 初始版本
- 支持 hcd-mgmt 和 hcd-applet 项目
- 支持多环境部署
- 集成 Docker 和 Docker Compose