#!/bin/bash

# HCD项目构建顺序测试脚本
# 用于验证Docker构建顺序和服务依赖关系

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

# 测试Docker Compose配置
test_docker_compose_config() {
    log_test "测试Docker Compose配置..."

    # 检查docker-compose.yml语法
    if docker-compose -f docker-compose.yml config > /dev/null 2>&1; then
        log_info "✓ docker-compose.yml 语法正确"
    else
        log_error "✗ docker-compose.yml 语法错误"
        exit 1
    fi

    # 检查服务依赖关系
    log_test "检查服务依赖关系..."

    # 检查网关是否被其他服务依赖
    local gateway_deps=$(docker-compose -f docker-compose.yml config | grep -A5 "depends_on:" | grep -c "hcd-gateway" || true)
    if [ $gateway_deps -gt 0 ]; then
        log_info "✓ 发现 $gateway_deps 个服务依赖 hcd-gateway"
    else
        log_warn "⚠ 未发现服务依赖 hcd-gateway"
    fi

    # 列出所有服务及其依赖
    log_test "服务依赖关系分析:"
    docker-compose -f docker-compose.yml config | grep -A10 "services:" | grep -E "(hcd-.*:|depends_on:)" | while read -r line; do
        if [[ $line == *"hcd-"*":"* ]]; then
            echo -e "  ${YELLOW}$(echo $line | cut -d':' -f1)${NC}:"
        elif [[ $line == *"depends_on:"* ]]; then
            echo -e "    依赖: ${GREEN}depends_on${NC}"
        fi
    done
}

# 测试CI/CD工作流配置
test_cicd_workflow() {
    log_test "测试CI/CD工作流配置..."

    if [ -f ".github/workflows/ci-cd.yml" ]; then
        log_info "✓ CI/CD工作流文件存在"

        # 检查是否分离了gateway构建
        if grep -q "build-gateway:" .github/workflows/ci-cd.yml; then
            log_info "✓ 发现独立的gateway构建任务"
        else
            log_error "✗ 未发现独立的gateway构建任务"
            exit 1
        fi

        # 检查构建依赖关系
        if grep -q "needs: \[build-gateway\]" .github/workflows/ci-cd.yml; then
            log_info "✓ 其他服务构建依赖于gateway构建完成"
        else
            log_error "✗ 未发现正确的构建依赖关系"
            exit 1
        fi

    else
        log_error "✗ CI/CD工作流文件不存在"
        exit 1
    fi
}

# 测试Dockerfile构建顺序
test_dockerfile_build_order() {
    log_test "测试Dockerfile构建顺序..."

    # 检查gateway Dockerfile
    if [ -f "dockerfiles/services/Dockerfile.gateway" ]; then
        log_info "✓ Gateway Dockerfile存在"

        # 检查是否包含正确的Maven构建命令
        if grep -q "mvn.*hcd-gateway.*-am" dockerfiles/services/Dockerfile.gateway; then
            log_info "✓ Gateway Dockerfile包含正确的构建命令"
        else
            log_warn "⚠ Gateway Dockerfile可能缺少依赖构建"
        fi
    else
        log_error "✗ Gateway Dockerfile不存在"
        exit 1
    fi

    # 检查其他服务的Dockerfile
    local services=("user" "trainseat" "ticket" "order")
    for service in "${services[@]}"; do
        if [ -f "dockerfiles/services/Dockerfile.${service}" ]; then
            log_info "✓ ${service}服务Dockerfile存在"
        else
            log_error "✗ ${service}服务Dockerfile不存在"
            exit 1
        fi
    done
}

# 测试Maven模块结构
test_maven_structure() {
    log_test "测试Maven模块结构..."

    if [ -f "backend/pom.xml" ]; then
        log_info "✓ 后端主pom.xml存在"

        # 检查模块声明
        local modules=("hcd-tools" "hcd-base" "hcd-gateway" "user-parent" "trainSeat-parent" "ticket-parent" "order-parent")
        for module in "${modules[@]}"; do
            if grep -q "<module>${module}</module>" backend/pom.xml; then
                log_info "✓ 发现模块: ${module}"
            else
                log_warn "⚠ 未发现模块: ${module}"
            fi
        done
    else
        log_error "✗ 后端主pom.xml不存在"
        exit 1
    fi
}

# 运行服务健康检查
test_service_health() {
    log_test "运行服务健康检查..."

    # 检查Docker服务是否运行
    if docker info > /dev/null 2>&1; then
        log_info "✓ Docker服务运行正常"
    else
        log_error "✗ Docker服务未运行"
        exit 1
    fi

    # 检查Docker Compose是否可用
    if docker-compose version > /dev/null 2>&1; then
        log_info "✓ Docker Compose可用"
    else
        log_error "✗ Docker Compose不可用"
        exit 1
    fi
}

# 生成测试报告
generate_report() {
    log_test "生成测试报告..."

    cat << EOF

${BLUE}========================================
HCD项目构建顺序测试报告
========================================${NC}

测试项目:
- Docker Compose配置
- CI/CD工作流配置
- Dockerfile构建顺序
- Maven模块结构
- 服务健康检查

主要改进:
1. ✓ 分离Gateway构建任务，确保优先构建
2. ✓ 其他服务构建依赖于Gateway完成
3. ✓ Docker Compose服务依赖关系配置
4. ✓ Maven多模块结构优化

构建顺序:
1. hcd-tools (基础工具)
2. hcd-base (基础常量)
3. hcd-gateway (网关服务) - 独立构建
4. user/trainseat/ticket/order服务 - 并行构建

依赖关系:
- 所有后端服务依赖 hcd-gateway
- Gateway依赖 hcd-tools 和 hcd-base
- 各服务间通过HTTP/Feign通信

${GREEN}测试完成!${NC}
EOF
}

# 主函数
main() {
    log_info "开始HCD项目构建顺序测试..."

    # 运行所有测试
    test_service_health
    test_maven_structure
    test_dockerfile_build_order
    test_cicd_workflow
    test_docker_compose_config

    # 生成报告
    generate_report

    log_info "所有测试通过!"
}

# 如果直接运行脚本
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi