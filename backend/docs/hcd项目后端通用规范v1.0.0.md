# HCD项目后端通用规范 v1.0.0

## 1. 项目概述

HCD项目后端采用微服务架构，基于Spring Cloud Alibaba技术栈构建分布式系统。本规范定义了技术选型、代码规范、数据库设计、接口规范、安全规范等技术标准，确保项目开发的一致性和可维护性。

## 2. 技术规范

### 2.1 核心技术栈

- **编程语言**: Java 17
- **框架**: Spring Boot 3.2.7
- **微服务**: Spring Cloud 2023.0.0
- **服务治理**: Spring Cloud Alibaba 2023.0.0.0-RC1
- **数据库**: MySQL 8.0.33
- **ORM框架**: MyBatis Plus 3.5.5
- **缓存**: Redis 7.x
- **消息队列**: RocketMQ 5.3.2
- **分布式事务**: Seata 2.5.0
- **存储**: Minio 8.5.10
- **文档**: Knife4j 4.5.0 (Swagger UI)
- **API文档**: SpringDoc OpenAPI 3
- **监控**: Micrometer + Prometheus + Grafana
- **链路追踪**: Sleuth + Zipkin
- **日志**: SLF4J + Logback + Logstash

### 2.2 微服务架构

```
hcd-backend/
├── hcd-tools/                  # 公共工具类
├── hcd-base/                   # 基础通用模块
├── hcd-gateway/                # API网关
├── user-parent/                # 用户管理服务
├── trainSeat-parent/           # 车次座位管理服务
├── ticket-parent/              # 票务管理服务
├── order-parent/               # 订单管理服务
└── system-parent/              # 系统管理服务
```

### 2.3 业务模块结构

每个业务模块采用分层架构：

```
xxx-parent/
├── xxx-api/                    # API接口和Feign客户端
├── xxx-biz/                    # 业务逻辑实现
└── xxx-model/                  # 数据模型和实体类
```

## 3. 代码规范

### 3.1 包结构规范

每个微服务模块遵循以下包结构：

```
top.flowerstardream.hcd.xxx/
├── ao/                         # 应用对象（DTO）
│   ├── dto/                    # 传输对象
│   ├── req/                    # 请求对象
│   └── res/                    # 响应对象
├── bo/                         # 业务实体对象
├── api/                        # API接口和Feign客户端
│   ├── app/              	# 小程序服务接口
│   ├── internal/               # 内部服务接口
│   ├── mgmt/               	# 后管服务接口
│   └── notify/                 # 支付通知回调接口
├── biz/                        # 业务逻辑实现
│   ├── client/                 # Feign客户端
│   ├── service/                # 业务服务接口
│   ├── service/impl/           # 业务服务实现
│   └── mapper/                 # 数据访问层
├── constant/                   # 常量定义
│   ├── XxxConstant.java        # 业务常量
│   ├── XxxExceptionEnum.java   # 异常枚举
│   └── XxxRedisPrefixConstant.java # Redis前缀常量
└── util/                       # 工具类
```

### 3.2 命名规范

#### 3.2.1 数据库命名

- 表名: `hcd_表名`，使用下划线分隔，小写字母
- 字段名: 小写字母，下划线分隔
- 主键: `id` bigint类型
- 时间字段: `create_time`, `update_time`
- 操作人字段: `create_person`, `update_person`

#### 3.2.2 代码命名

- 类名: PascalCase命名法
- 方法名: camelCase命名法
- 常量: 全大写，下划线分隔
- 包名: 全小写，点号分隔
- 接口名: 以 `I`开头，如 `IUserService`
- 实现类: 以 `Impl`结尾，如 `UserServiceImpl`
- 枚举类: 以 `Enum`结尾，如 `UserStatusEnum`

### 3.3 标准代码模板

#### 3.3.1 服务实现类模板

```java
package top.flowerstardream.hcd.xxx.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.xxx.biz.mapper.XxxMapper;
import top.flowerstardream.hcd.xxx.biz.service.IXxxService;
import top.flowerstardream.hcd.xxx.bo.eo.XxxEO;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.xxx.constant.XxxExceptionEnum.*;

/**
 * Xxx服务实现类
 *
 * @author 开发者名称
 * @date yyyy-MM-dd
 */
@Slf4j
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxEO> implements IXxxService {

    @Resource
    private XxxMapper xxxMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void xxxMethod() {
        log.info("xxxMethod start, param: {}", param);
        try {
            // 业务逻辑实现
            log.info("xxxMethod end successfully");
        } catch (Exception e) {
            log.error("xxxMethod error", e);
            throw new BusinessException(XxxExceptionEnum.XXX_ERROR);
        }
    }
}
```

#### 3.3.2 控制器类模板

```java
package top.flowerstardream.hcd.xxx.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.xxx.ao.req.XxxREQ;
import top.flowerstardream.hcd.xxx.ao.res.XxxRES;
import top.flowerstardream.hcd.xxx.biz.service.IXxxService;

/**
 * Xxx控制器
 *
 * @author 开发者名称
 * @date yyyy-MM-dd
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/xxx")
@Tag(name = "Xxx接口", description = "Xxx相关接口")
public class XxxController {

    @Resource
    private IXxxService xxxService;

    @PostMapping("/xxx")
    @Operation(summary = "Xxx操作", description = "Xxx操作描述")
    public Result<XxxRES> xxxMethod(@Valid @RequestBody XxxREQ req) {
        log.info("xxxMethod start, req: {}", req);
        try {
            // 调用服务层方法
            return Result.successResult(result);
        } catch (Exception e) {
            log.error("xxxMethod error", e);
            return Result.errorResult(e.getMessage());
        }
    }
}
```

#### 3.3.3 实体类模板

```java
package top.flowerstardream.hcd.xxx.bo.eo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.flowerstardream.hcd.base.bo.BaseEO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Xxx实体类
 *
 * @author 开发者名称
 * @date yyyy-MM-dd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("hcd_xxx")
public class XxxEO extends BaseEO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 业务字段
     */
    private String xxxField;

    /**
     * 状态 1-正常 0-禁用
     */
    private Integer status;

    /**
     * 删除标识 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
```

## 4. 数据库设计规范

### 4.1 通用字段规范

所有表必须包含以下字段：

```sql
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`create_person` varchar(30) NOT NULL DEFAULT 'system' COMMENT '创建人',
`update_person` varchar(30) NOT NULL DEFAULT 'system' COMMENT '更新者'
```

### 4.2 数据类型规范

- 主键ID: bigint AUTO_INCREMENT
- 状态字段: int (1-正常, 0-禁用)
- 删除标识: tinyint(1) (0-未删除, 1-已删除)
- 金额: decimal(10,2)
- 时间: datetime
- 字符串: varchar(长度根据需求确定)
- 长文本: text
- JSON数据: json

### 4.3 索引规范

- 主键自动创建聚簇索引
- 外键字段必须创建索引
- 业务查询字段创建复合索引
- 状态字段创建索引
- 时间范围查询字段创建索引
- 唯一约束创建唯一索引

### 4.4 表设计模板

```sql
CREATE TABLE `hcd_xxx` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `xxx_field` varchar(100) DEFAULT NULL COMMENT '业务字段',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_person` varchar(30) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `update_person` varchar(30) NOT NULL DEFAULT 'system' COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='xxx表';
```

## 5. 微服务通信规范

### 5.1 服务发现

- 使用Nacos作为服务注册中心
- 服务名格式: `hcd-xxx`
- 分组: `DEFAULT_GROUP`
- 命名空间: 按环境区分hcd-(dev/test/prod)

### 5.2 服务调用

- 内部服务调用使用OpenFeign
- 统一通过API网关进行外部访问
- 服务降级使用Sentinel
- 调用超时设置: 读取3秒，连接1秒
- 重试机制: 默认不重试，重要业务可配置重试

### 5.3 配置管理

- 使用Nacos作为配置中心
- 配置文件格式: YAML
- 配置分组: `DEFAULT_GROUP`
- 环境隔离: dev/test/prod
- 配置热更新支持

### 5.4 服务分层调用规范

```
外部请求 → API网关 → 业务服务 → 数据访问层
     ↓              ↓           ↓
   路由转发      业务逻辑     数据库操作
```

## 6. 安全规范

### 6.1 认证授权

- JWT Token认证，有效期24小时
- 基于角色的访问控制(RBAC)
- 接口权限注解：`@PreAuthorize("hasRole('ADMIN')")`
- 用户权限缓存到Redis
- 支持多终端登录

### 6.2 数据安全

- 敏感数据加密存储（AES加密）
- SQL注入防护（MyBatis参数化查询）
- 接口防重放攻击（Token + 时间戳）
- 密码加密（BCrypt）

### 6.3 传输安全

- HTTPS协议传输
- 接口签名验证
- 请求频率限制
- IP白名单控制

## 7. 异常处理规范

### 7.1 异常分类

- 业务异常: BusinessException（用户可理解的业务错误）
- 系统异常: SystemException（系统内部错误）
- 参数异常: ParamException（参数校验错误）
- 认证异常: AuthException（认证授权错误）

### 7.2 异常处理原则

- 统一异常处理切面
- 异常信息国际化支持
- 异常日志完整记录
- 友好错误信息返回
- 异常链保持完整

### 7.3 异常定义规范

```java
@Getter
@AllArgsConstructor
public enum XxxExceptionEnum implements ICustomError {
    XXX_NOT_FOUND(10001, "xxx不存在"),
    XXX_EXISTS(10002, "xxx已存在"),
    XXX_PARAM_ERROR(10003, "xxx参数错误");

    private final Integer code;
    private final String message;
}
```

## 8. 缓存规范

### 8.1 缓存策略

- 查询频率高的数据使用缓存
- 缓存过期时间统一配置
- 缓存穿透使用布隆过滤器
- 缓存雪崩预防（随机过期时间）
- 缓存击穿防护（互斥锁）

### 8.2 缓存命名规范

```
hcd:module:business:id              # 单条数据缓存
hcd:module:list:condition           # 列表数据缓存
hcd:module:count:condition          # 统计数据缓存
```

### 8.3 缓存更新策略

- 写入时更新缓存
- 删除时清除缓存
- 定时刷新缓存
- 版本号控制缓存更新

## 9. 消息队列规范

### 9.1 消息格式

```json
{
  "messageId": "uuid",
  "timestamp": 1640995200000,
  "source": "service-name",
  "type": "EVENT_TYPE",
  "data": {
    // 业务数据
  }
}
```

### 9.2 消息主题规范

- 格式: `hcd-{module}-{event}-topic`
- 业务主题: `hcd-order-create-topic`
- 系统主题: `hcd-system-log-topic`
- 死信主题: `hcd-{module}-dlq-topic`

### 9.3 消息消费规范

- 幂等性处理（基于messageId）
- 失败重试机制（指数退避）
- 死信队列处理
- 消费进度监控
- 消息顺序保证

## 10. 分布式事务规范

### 10.1 事务类型

- 强一致性事务: 使用Seata AT模式
- 最终一致性: 使用消息队列
- TCC模式: 用于复杂业务场景
- SAGA模式: 用于长事务处理

### 10.2 Seata配置规范

```yaml
seata:
  enabled: true
  application-id: ${spring.application.name}
  tx-service-group: hcd-group
  config:
    type: nacos
    nacos:
      server-addr: ${spring.cloud.nacos.config.server-addr}
      namespace: ${spring.cloud.nacos.config.namespace}
      group: SEATA_GROUP
  registry:
    type: nacos
    nacos:
      server-addr: ${spring.cloud.nacos.discovery.server-addr}
      namespace: ${spring.cloud.nacos.discovery.namespace}
      group: SEATA_GROUP
```

### 10.3 事务设计原则

- 避免长事务
- 事务边界清晰
- 补偿机制完善
- 事务超时设置合理

## 11. 监控规范

### 11.1 业务指标监控

- 接口调用次数
- 接口响应时间
- 业务成功率
- 异常发生频率
- 数据吞吐量

### 11.2 技术指标监控

- JVM内存使用率
- 数据库连接数
- Redis连接数
- 消息队列积压量
- 服务健康状态
- 错误率统计

### 11.3 告警规则

- 接口响应时间 > 1秒
- 错误率 > 5%
- 数据库连接数 > 80%
- 消息队列积压 > 1000
- 服务不可用
- JVM内存使用率 > 90%

### 11.4 监控配置

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

## 12. 日志规范

### 12.1 日志格式

```
[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] %-5level [%logger{50}] - [%X{traceId}] [%X{userId}] %msg%n
```

### 12.2 日志级别使用

- **ERROR**: 系统异常、业务异常
- **WARN**: 潜在问题、非预期情况
- **INFO**: 业务关键操作、状态变更
- **DEBUG**: 详细调试信息

### 12.3 日志规范

- 统一使用SLF4J + Logback
- 关键业务操作必须记录日志
- 异常日志必须包含堆栈信息
- 敏感信息脱敏处理
- 日志文件按天归档

## 13. 文档规范

### 13.1 API文档

- 使用OpenAPI 3.0规范
- 接口说明完整详细
- 示例请求响应提供
- 错误码说明清晰
- 版本变更记录完整

### 13.2 代码注释

- 类级别注释说明用途
- 方法级别注释说明功能和参数
- 复杂业务逻辑详细注释
- 常量定义说明含义
- 异常抛出说明原因

### 13.3 文档模板

```java
/**
 * 功能描述
 *
 * @author 作者
 * @date 创建时间
 * @since 版本
 */
```

## 14. 测试规范

### 14.1 单元测试要求

- 核心业务逻辑覆盖率 ≥ 80%
- 使用JUnit 5 + Mockito
- 测试类命名：`XxxServiceTest`
- 测试方法命名：`testXxxScenario()`
- 测试数据使用内存数据库

### 14.2 集成测试要求

- 关键业务流程测试
- 使用@SpringBootTest
- 测试数据库使用H2
- 测试数据自动回滚
- 外部服务使用Mock

### 14.3 测试规范

```java
@Test
void testXxxSuccess() {
    // Given
    XxxREQ req = XxxREQ.builder()
        .field(value)
        .build();

    // When
    Result<XxxRES> result = xxxController.xxxMethod(req);

    // Then
    assertThat(result.getCode()).isEqualTo(200);
    assertThat(result.getData()).isNotNull();
}
```

## 15. 部署规范

### 15.1 容器化部署

- 基础镜像：openjdk:17-jre-slim
- 镜像标签规范：`{服务名}:{版本号}-{环境}-{构建号}`
- 健康检查：/actuator/health
- 资源限制：CPU 1核，内存 2GB
- 时区设置：Asia/Shanghai

### 15.2 Dockerfile模板

```dockerfile
FROM openjdk:17-jre-slim

LABEL maintainer="HCD Team"
LABEL version="1.0.0"
LABEL description="HCD XXX Service"

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用目录
WORKDIR /app

# 复制JAR文件
COPY target/xxx-service-*.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "app.jar"]
```

### 15.3 环境配置

```yaml
# application-dev.yml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://mysql-dev:3306/hcd?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

# application-prod.yml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:mysql://mysql-prod:3306/hcd?useUnicode=true&characterEncoding=utf8&useSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

### 15.4 部署环境

- 开发环境: dev
- 测试环境: test
- 预生产环境: pre
- 生产环境: prod

## 16. 性能优化规范

### 16.1 数据库优化

- 合理使用索引
- 避免全表扫描
- 分页查询优化
- 连接池参数调优
- 慢查询监控和优化

### 16.2 缓存优化

- 缓存预热机制
- 缓存穿透防护
- 缓存雪崩预防
- 缓存命中率监控
- 大Key和热Key处理

### 16.3 JVM优化

- 堆内存合理设置
- GC算法选择
- 线程池参数调优
- 连接池参数调优
- 监控指标采集

---

**文档版本**: v1.0.0

**创建时间**: 2025年11月28日
**更新记录**:

- v1.0.0: 初始版本，建立后端开发规范

**文档状态**: 正式发布
**适用范围**: HCD项目后端开发团队、架构师、技术负责人
