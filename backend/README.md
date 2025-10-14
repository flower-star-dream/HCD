# HCD 微服务后端项目

## 项目简介

HCD是一个基于Spring Cloud的微服务架构后端项目，采用领域驱动设计(DDD)思想，实现了用户管理、座位管理、票务管理、支付通知和系统管理等核心业务模块。

## 技术栈

### 核心技术
- **Spring Boot 3.2.0** - 核心框架
- **Spring Cloud 2023.0.0** - 微服务框架
- **Spring Cloud Alibaba 2023.0.0.0-RC1** - 阿里巴巴微服务组件
- **Java 17** - 编程语言
- **Maven** - 项目构建工具

### 数据存储
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存数据库
- **Elasticsearch** - 搜索引擎
- **MinIO** - 对象存储

### 中间件
- **Nacos** - 注册中心与配置中心
- **RabbitMQ** - 消息队列
- **Jenkins** - CI/CD持续集成

### 项目结构

```
hcd-backend/
├── hcd-tools/                          # 通用工具库
├── user-parent/                        # 用户服务
│   ├── user-ao/                       # 数据传输对象DTO
│   ├── user-api/                      # 接口定义
│   ├── user-biz/                      # 业务实现
│   ├── user-bo/                       # 实体对象EO
│   └── user-constant/                 # 枚举常量
├── trainSeat-parent/                   # 座位服务
│   ├── trainSeat-ao/
│   ├── trainSeat-api/
│   ├── trainSeat-biz/
│   ├── trainSeat-bo/
│   └── trainSeat-constant/
├── ticket-parent/                      # 票务服务
│   ├── ticket-ao/
│   ├── ticket-api/
│   ├── ticket-biz/
│   ├── ticket-bo/
│   └── ticket-constant/
├── payNotify-parent/                   # 支付通知服务
│   ├── payNotify-ao/
│   ├── payNotify-api/
│   ├── payNotify-biz/
│   ├── payNotify-bo/
│   └── payNotify-constant/
└── system-parent/                      # 系统管理服务
    ├── system-ao/
    ├── system-api/
    ├── system-biz/
    ├── system-bo/
    └── system-constant/
```

## 快速开始

### 环境准备

1. **Java 17** 或以上版本
2. **Maven 3.8+**
3. **MySQL 8.0**
4. **Redis**
5. **RabbitMQ**
6. **Nacos**
7. **Elasticsearch**
8. **MinIO**

### 启动步骤

1. **克隆项目**
   ```bash
   git clone [项目地址]
   cd hcd-backend
   ```

2. **启动依赖服务**
   - 启动MySQL、Redis、RabbitMQ、Nacos、Elasticsearch、MinIO

3. **创建数据库**
   ```sql
   CREATE DATABASE hcd DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **编译项目**
   ```bash
   mvn clean install -DskipTests
   ```

5. **启动服务**
   按需启动各个微服务模块

## 配置说明

### 数据库配置
在 `application.yml` 中配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hcd?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: your_password
```

### Nacos配置
确保Nacos服务已启动，并在Nacos控制台创建对应的命名空间和服务配置。

### 服务注册
每个微服务启动后都会自动注册到Nacos注册中心。

## 开发规范

### 包命名规范
- 基础包名：`top.flower.star.dream.hcd`
- 服务包名：`top.flower.star.dream.hcd.[service-name]`

### 模块职责
- **ao模块**：存放DTO（Data Transfer Object）
- **api模块**：存放接口定义和Feign客户端
- **biz模块**：存放业务逻辑实现
- **bo模块**：存放实体对象（Entity Object）
- **constant模块**：存放枚举类和常量

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用统一的异常处理机制
- 接口响应统一使用Result格式
- 日志记录使用SLF4J

## 部署说明

### Docker部署
项目支持Docker容器化部署，每个服务都有对应的Dockerfile。

### Jenkins CI/CD
使用Jenkins实现持续集成和持续部署，Jenkinsfile已配置完成。

## 监控和日志

### 监控
- 使用Spring Boot Actuator进行健康检查
- 集成Prometheus + Grafana进行监控

### 日志
- 使用Logback进行日志管理
- 支持ELK日志收集和分析

## 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱：your-email@example.com
- GitHub Issues：提交Issue

## 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。